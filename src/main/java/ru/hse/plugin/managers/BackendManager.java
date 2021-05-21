package ru.hse.plugin.managers;

import com.google.api.client.http.*;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import ru.hse.plugin.data.*;
import ru.hse.plugin.exceptions.HttpException;
import ru.hse.plugin.exceptions.UnauthorizedException;
import ru.hse.plugin.utils.PropertiesUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BackendManager {
    private static final HttpTransport TRANSPORT = new ApacheHttpTransport();
    private static final HttpRequestFactory REQUEST_FACTORY = TRANSPORT.createRequestFactory();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static final String REPLACEMENT = "${}";
    private static final String API_URL = new PropertiesUtils("/constants/constants.properties").getKey("apiUrl", "");
    private final String USER_URL;
    private static final String TEAMS_URL = API_URL + "teams/";
    private static final String TEAM_PROBLEMS_URL = TEAMS_URL + REPLACEMENT + "/problems/";
    private static final String FOLDERS_URL = API_URL + "folders/";
    private static final String FOLDER_SUB_FOLDERS_URL = FOLDERS_URL + REPLACEMENT + "/folders/";
    private static final String FOLDER_PROBLEMS_URL = FOLDERS_URL + REPLACEMENT + "/problems/";
    private static final String PROBLEMS_URL = API_URL + "problems/";
    private static final String SUBMISSIONS_URL = API_URL + "submissions/";
    private static final String PROBLEM_SUBMISSIONS_URL = PROBLEMS_URL + REPLACEMENT + "/submissions/";
    private final String USER_PROBLEMS_URL;
    private static final int UNAUTHORIZED_CODE = 401;

    private final Credentials credentials;

    public BackendManager(Credentials credentials) {
        this.credentials = credentials;
        USER_URL = API_URL + "users/" + credentials.getLogin() + "/";
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

    public List<Team> getTeams() throws UnauthorizedException, HttpException {
        List<Team> list = new ArrayList<>();
        User user = getUser();
        for (String teamName : user.getTeams()) {
            list.add(getTeam(teamName));
        }
        return list;
    }

    public Team getTeam(String teamName) throws UnauthorizedException, HttpException {
        return getData(TEAMS_URL + teamName, Team.class);
    }

    public User getUser() throws UnauthorizedException, HttpException {
        return getData(USER_URL, User.class);
    }

    public List<TreeFile> getRootFiles(Team team) throws UnauthorizedException, HttpException {
        List<TreeFile> list = new ArrayList<>();
        list.add(getFolderWithAllProblems(team));
        Folder rootFolder = getData(FOLDERS_URL + team.getRootFolderId(), Folder.class);
        list.addAll(getFolderFiles(rootFolder.getId()));
        return list;
    }

    public List<TreeFile> getPersonalRootFiles() throws UnauthorizedException, HttpException {
        List<TreeFile> list = new ArrayList<>();
        list.add(getFolderWithAllProblems());
        User user = getUser();
        Folder rootFolder = getData(FOLDERS_URL + user.getRootFolderId(), Folder.class);
        list.addAll(getFolderFiles(rootFolder.getId()));
        return list;
    }

    private Folder getFolderWithAllProblems() throws UnauthorizedException, HttpException {
        Folder all = new Folder("All", -1);
        all.setLoaded();
        List<Problem> allProblems = getAllProblems();
        saveProblemsToPool(allProblems);
        getProblemsReferences(allProblems).forEach(all::add);
        return all;
    }

    private Folder getFolderWithAllProblems(Team team) throws UnauthorizedException, HttpException {
        Folder all = new Folder("All", -1);
        all.setLoaded();
        List<Problem> allProblems = getAllProblems(team);
        saveProblemsToPool(allProblems);
        getProblemsReferences(allProblems).forEach(all::add);
        return all;
    }

    private List<Problem> getAllProblems() throws UnauthorizedException, HttpException {
        Problem[] problems = getData(USER_PROBLEMS_URL, Problem[].class);
        return Arrays.asList(problems);
    }

    private List<Problem> getAllProblems(Team team) throws UnauthorizedException, HttpException {
        Problem[] problems = getData(TEAM_PROBLEMS_URL.replace(REPLACEMENT, team.getName()), Problem[].class);
        return Arrays.asList(problems);
    }

    public List<TreeFile> getFolderFiles(String folderId) throws UnauthorizedException, HttpException {
        List<TreeFile> files = new ArrayList<>();
        files.addAll(getFolderSubFolders(folderId));
        List<Problem> problems = getFolderProblems(folderId);
        saveProblemsToPool(problems);
        files.addAll(getProblemsReferences(problems));
        return files;
    }

    private Folder getFolder(String foldersId) throws UnauthorizedException, HttpException {
        return getData(FOLDERS_URL + foldersId, Folder.class);
    }

    private List<Folder> getFolderSubFolders(String folderId) throws UnauthorizedException, HttpException {
        Folder[] folders = getData(FOLDER_SUB_FOLDERS_URL.replace(REPLACEMENT, folderId), Folder[].class);
        return Arrays.asList(folders);
    }

    private List<Problem> getFolderProblems(String folderId) throws UnauthorizedException, HttpException {
        Problem[] problems = getData(FOLDER_PROBLEMS_URL.replace(REPLACEMENT, folderId), Problem[].class);
        return Arrays.asList(problems);
    }


    private Problem loadProblem(String problemId) throws UnauthorizedException, HttpException {
        return getData(PROBLEMS_URL + problemId, Problem.class);
    }

    public Submission loadSubmission(String submissionId) throws UnauthorizedException, HttpException {
        return getData(SUBMISSIONS_URL + submissionId, Submission.class);
    }

    public List<Submission> getProblemSubmissions(Problem problem) throws UnauthorizedException, HttpException {
        Submission[] submissions = getData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), Submission[].class);
        return Arrays.asList(submissions);
    }

    public void sendSubmission(Problem problem, Submission submission) throws UnauthorizedException, HttpException {
        sendData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), submission);
    }

    public void sendProblemState(Problem problem) throws UnauthorizedException, HttpException {
        updateData(PROBLEMS_URL + problem.getId(), problem);
    }

    private void saveProblemsToPool(Iterable<Problem> iterable) {
        ProblemPool problemPool = ProblemPool.getInstance();
        iterable.forEach(problemPool::addProblem);
    }

    private List<ProblemReference> getProblemsReferences(List<Problem> list) {
        return list.stream().map(ProblemReference::new).collect(Collectors.toList());
    }

    private <T> T getData(String URL, Class<T> tClass) throws UnauthorizedException, HttpException {
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
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }

    private void sendData(String URL, Object object) throws UnauthorizedException, HttpException {
        GenericUrl url = new GenericUrl(URL);
        try {
            JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, object);
            HttpMediaType mediaType = new HttpMediaType("application/json");
            mediaType.setCharsetParameter(StandardCharsets.UTF_8);
            content.setMediaType(mediaType);
            HttpRequest req = REQUEST_FACTORY.buildPostRequest(url, content);
            HttpResponse resp = req.execute();
            resp.disconnect();
            //TODO: проверять status code
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE) {
                throw new UnauthorizedException();
            }
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }

    private void updateData(String URL, Object object) throws UnauthorizedException, HttpException {
        GenericUrl url = new GenericUrl(URL);
        try {
            JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, object);
            HttpMediaType mediaType = new HttpMediaType("application/merge-patch+json");
            mediaType.setCharsetParameter(StandardCharsets.UTF_8);
            content.setMediaType(mediaType);
            HttpRequest req = REQUEST_FACTORY.buildPatchRequest(url, content);
            HttpResponse resp = req.execute();
            resp.disconnect();
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE) {
                throw new UnauthorizedException();
            }
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }
}
