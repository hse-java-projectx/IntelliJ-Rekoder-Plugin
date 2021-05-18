package ru.hse.plugin.managers;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import ru.hse.plugin.data.*;
import ru.hse.plugin.exceptions.UnauthorizedException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BackendManager {
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final HttpRequestFactory REQUEST_FACTORY = TRANSPORT.createRequestFactory();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static final String REPLACEMENT = "${}";
    private static final String API_URL = "https://api.rekoder.xyz/";
    private final String USER_URL;
    private static final String TEAMS_URL = API_URL + "teams/";
    private static final String TEAM_FOLDERS_URL = TEAMS_URL + REPLACEMENT + "/folders/";
    private static final String FOLDER_SUB_FOLDERS_URL = API_URL + "folders/" + REPLACEMENT + "/folders/";
    private static final String FOLDER_PROBLEMS_URL = API_URL + "folders/" + REPLACEMENT + "/problems/";
    private static final String PROBLEMS_URL = API_URL + "problems/";
    private static final String SUBMISSIONS_URL = API_URL + "submissions/";
    private static final String PROBLEM_SUBMISSIONS_URL = PROBLEMS_URL + REPLACEMENT + "/submissions/";
    private final String USER_FOLDERS_URL;
    private final String USER_PROBLEMS_URL;
    private static final int UNAUTHORIZED_CODE = 401;

    private final Credentials credentials;

    public BackendManager(Credentials credentials) {
        this.credentials = credentials;
        USER_URL = API_URL + "users/" + credentials.getLogin() + "/";
        USER_FOLDERS_URL = USER_URL + "folders/";
        USER_PROBLEMS_URL = USER_URL + "problems/";
    }

    // returns token
    @Nullable
    public String login(String login, String password) {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "token";
    }

    public List<Team> getTeams() throws UnauthorizedException {
        List<Team> list = new ArrayList<>();
        User user = getUser();
        for (String teamName : user.getTeams()) {
            list.add(getTeam(teamName));
        }
        return list;
    }

    public Team getTeam(String teamName) throws UnauthorizedException {
        return getData(TEAMS_URL + teamName, Team.class);
    }

    public User getUser() throws UnauthorizedException {
        return getData(USER_URL, User.class);
    }

    public List<Folder> getRootFolders(Team team) throws UnauthorizedException { // TODO
        Folder[] folders = getData(TEAM_FOLDERS_URL.replace(REPLACEMENT, team.getName()), Folder[].class);
        return Arrays.asList(folders);
    }

    public List<Folder> getPersonalRootFolders() throws UnauthorizedException {
        List<Folder> list = new ArrayList<>();
        list.add(getFolderWithAllProblems());
        Folder[] folders = getData(USER_FOLDERS_URL, Folder[].class);
        list.addAll(Arrays.asList(folders));
        return list;
    }

    private Folder getFolderWithAllProblems() throws UnauthorizedException {
        Folder all = new Folder("All", -1);
        all.setLoaded();
        List<Problem> allProblems = getAllProblems();
        saveProblemsToPool(allProblems);
        getProblemsReferences(allProblems).forEach(all::add);
        return all;
    }

    private List<Problem> getAllProblems() throws UnauthorizedException {
        Problem[] problems = getData(USER_PROBLEMS_URL, Problem[].class);
        for (Problem problem : problems) {
            problem.setSubmissions(getProblemSubmissions(problem));
        }
        return Arrays.asList(problems);
    }

    public List<TreeFile> loadFolder(String folderId) throws UnauthorizedException {
        List<TreeFile> files = new ArrayList<>();
        files.addAll(getFolderSubFolders(folderId));
        List<Problem> problems = getFolderProblems(folderId);
        saveProblemsToPool(problems);
        files.addAll(getProblemsReferences(problems));
        return files;
    }

    private List<Folder> getFolderSubFolders(String folderId) throws UnauthorizedException {
        Folder[] folders = getData(FOLDER_SUB_FOLDERS_URL.replace(REPLACEMENT, folderId), Folder[].class);
        return Arrays.asList(folders);
    }

    private List<Problem> getFolderProblems(String folderId) throws UnauthorizedException {
        Problem[] problems = getData(FOLDER_PROBLEMS_URL.replace(REPLACEMENT, folderId), Problem[].class);
        for (Problem problem : problems) {
            problem.setSubmissions(getProblemSubmissions(problem));
        }
        return Arrays.asList(problems);
    }


    private Problem loadProblem(String problemId) throws UnauthorizedException {
        return getData(PROBLEMS_URL + problemId, Problem.class);
    }

    public Submission loadSubmission(String submissionId) throws UnauthorizedException {
        return getData(SUBMISSIONS_URL + submissionId, Submission.class);
    }

    public List<Submission> getProblemSubmissions(Problem problem) throws UnauthorizedException {
        Submission[] submissions = getData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), Submission[].class);
        return Arrays.asList(submissions);
    }

    public void sendSubmission(Problem problem, Submission submission) throws UnauthorizedException {
        System.out.println("Sending");
        sendData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), submission);
    }

    private void saveProblemsToPool(Iterable<Problem> iterable) {
        ProblemPool problemPool = ProblemPool.getInstance();
        iterable.forEach(problemPool::addProblem);
    }

    private List<ProblemReference> getProblemsReferences(List<Problem> list) {
        return list.stream().map(ProblemReference::new).collect(Collectors.toList());
    }

    private <T> T getData(String URL, Class<T> tClass) throws UnauthorizedException {
        GenericUrl url = new GenericUrl(URL);
        try {
            HttpRequest req = REQUEST_FACTORY.buildGetRequest(url);
            req.setParser(new JsonObjectParser(JSON_FACTORY));
            HttpResponse resp = req.execute();
            //TODO: проверять status code
            return resp.parseAs(tClass);
        } catch (HttpResponseException ex) {
            ex.printStackTrace();
            if (ex.getStatusCode() == UNAUTHORIZED_CODE) {
                throw new UnauthorizedException();
            }
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendData(String URL, Object object) throws UnauthorizedException {
        GenericUrl url = new GenericUrl(URL);
        try {
            HttpRequest req = REQUEST_FACTORY.buildPostRequest(url, new JsonHttpContent(JSON_FACTORY, object));
            HttpResponse resp = req.execute();
            //TODO: проверять status code
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE) {
                throw new UnauthorizedException();
            }
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            //TODO: нормальные исключения
            throw new RuntimeException(ex);
        }
    }
}
