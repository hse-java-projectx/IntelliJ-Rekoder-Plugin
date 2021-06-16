package ru.hse.plugin.managers;

import com.google.api.client.http.*;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Key;
import org.apache.commons.io.IOUtils;
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
    private static final String LOGIN_URL = API_URL + "login";
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
    private final String USER_TEAMS_URL;
    private static final int UNAUTHORIZED_CODE = 401;
    private static final int FORBIDDEN_CODE = 403;

    private final Credentials credentials;

    public BackendManager(Credentials credentials) {
        this.credentials = credentials;
        USER_URL = API_URL + "users/" + credentials.getLogin() + "/";
        USER_PROBLEMS_URL = USER_URL + "problems/";
        USER_TEAMS_URL = USER_URL + "teams/";
    }

    // returns token
    @Nullable
    public String login(String login, String password) throws HttpException {
        LoginInfo loginInfo = new LoginInfo(login, password);
        GenericUrl url = new GenericUrl(LOGIN_URL);
        try {
            JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, loginInfo);
            HttpMediaType mediaType = new HttpMediaType("application/json");
            mediaType.setCharsetParameter(StandardCharsets.UTF_8);
            content.setMediaType(mediaType);
            HttpRequest req = REQUEST_FACTORY.buildPostRequest(url, content);
            req.setParser(new JsonObjectParser(JSON_FACTORY));
            HttpResponse resp = req.execute();
            return resp.getHeaders().getAuthorization();
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE) {
                throw new UnauthorizedException(ex);
            }
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }

    public List<Team> getTeams() throws HttpException {
        Team[] teams = getData(USER_TEAMS_URL, Team[].class);
        return Arrays.asList(teams);
    }

    public Team getTeam(String teamName) throws HttpException {
        return getData(TEAMS_URL + teamName, Team.class);
    }

    public User getUser() throws HttpException {
        return getData(USER_URL, User.class);
    }

    public List<TreeFile> getRootFiles(Team team) throws HttpException {
        List<TreeFile> list = new ArrayList<>();
        list.add(getFolderWithAllProblems(team));
        Folder rootFolder = getData(FOLDERS_URL + team.getRootFolderId(), Folder.class);
        list.addAll(getFolderFiles(rootFolder.getId()));
        return list;
    }

    public List<TreeFile> getPersonalRootFiles() throws HttpException {
        List<TreeFile> list = new ArrayList<>();
        list.add(getFolderWithAllProblems());
        User user = getUser();
        Folder rootFolder = getData(FOLDERS_URL + user.getRootFolderId(), Folder.class);
        list.addAll(getFolderFiles(rootFolder.getId()));
        return list;
    }

    private Folder getFolderWithAllProblems() throws HttpException {
        Folder all = new Folder("All", -1);
        all.setLoaded();
        List<Problem> allProblems = getAllProblems();
        saveProblemsToPool(allProblems);
        getProblemsReferences(allProblems).forEach(all::add);
        return all;
    }

    private Folder getFolderWithAllProblems(Team team) throws HttpException {
        Folder all = new Folder("All", -1);
        all.setLoaded();
        List<Problem> allProblems = getAllProblems(team);
        saveProblemsToPool(allProblems);
        getProblemsReferences(allProblems).forEach(all::add);
        return all;
    }

    private List<Problem> getAllProblems() throws HttpException {
        Problem[] problems = getData(USER_PROBLEMS_URL, Problem[].class);
        return Arrays.asList(problems);
    }

    private List<Problem> getAllProblems(Team team) throws HttpException {
        Problem[] problems = getData(TEAM_PROBLEMS_URL.replace(REPLACEMENT, team.getId()), Problem[].class);
        return Arrays.asList(problems);
    }

    public List<TreeFile> getFolderFiles(String folderId) throws HttpException {
        List<TreeFile> files = new ArrayList<>();
        files.addAll(getFolderSubFolders(folderId));
        List<Problem> problems = getFolderProblems(folderId);
        saveProblemsToPool(problems);
        files.addAll(getProblemsReferences(problems));
        return files;
    }

    public Problem getOriginalProblem(Problem problem) throws HttpException {
        if (problem.getOriginalProblemId() == null) {
            return problem;
        }
        return loadProblem(problem.getOriginalProblemId().toString());
    }

    private Folder getFolder(String foldersId) throws HttpException {
        return getData(FOLDERS_URL + foldersId, Folder.class);
    }

    private List<Folder> getFolderSubFolders(String folderId) throws HttpException {
        Folder[] folders = getData(FOLDER_SUB_FOLDERS_URL.replace(REPLACEMENT, folderId), Folder[].class);
        return Arrays.asList(folders);
    }

    private List<Problem> getFolderProblems(String folderId) throws HttpException {
        Problem[] problems = getData(FOLDER_PROBLEMS_URL.replace(REPLACEMENT, folderId), Problem[].class);
        return Arrays.asList(problems);
    }


    private Problem loadProblem(String problemId) throws HttpException {
        return getData(PROBLEMS_URL + problemId, Problem.class);
    }

    public Submission loadSubmission(String submissionId) throws HttpException {
        return getData(SUBMISSIONS_URL + submissionId, Submission.class);
    }

    public List<Submission> getProblemSubmissions(Problem problem) throws HttpException {
        Submission[] submissions = getData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), Submission[].class);
        return Arrays.asList(submissions);
    }

    public Submission sendSubmission(Problem problem, Submission submission) throws HttpException {
        return sendData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), submission, Submission.class);
    }

    public void sendProblemState(Problem problem) throws HttpException {
        updateData(PROBLEMS_URL + problem.getId(), problem);
    }

    private void saveProblemsToPool(Iterable<Problem> iterable) {
        ProblemPool problemPool = ProblemPool.getInstance();
        iterable.forEach(problemPool::addProblem);
    }

    private List<ProblemReference> getProblemsReferences(List<Problem> list) {
        return list.stream().map(ProblemReference::new).collect(Collectors.toList());
    }

    private <T> T getData(String URL, Class<T> tClass) throws HttpException {
        GenericUrl url = new GenericUrl(URL);
        try {
            HttpRequest req = REQUEST_FACTORY.buildGetRequest(url);
            req.setParser(new JsonObjectParser(JSON_FACTORY));
            HttpResponse resp = req.execute();
            return resp.parseAs(tClass);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE || ex.getStatusCode() == FORBIDDEN_CODE) {
                throw new UnauthorizedException();
            }
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }

    private <T> T sendData(String URL, T object, Class<T> tClass) throws HttpException {
        GenericUrl url = new GenericUrl(URL);
        try {
            JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, object);
            HttpMediaType mediaType = new HttpMediaType("application/json");
            mediaType.setCharsetParameter(StandardCharsets.UTF_8);
            content.setMediaType(mediaType);
            HttpRequest req = REQUEST_FACTORY.buildPostRequest(url, content);
            req.getHeaders().setAuthorization(credentials.getToken());
            req.setParser(new JsonObjectParser(JSON_FACTORY));
            HttpResponse resp = req.execute();
            return resp.parseAs(tClass);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE || ex.getStatusCode() == FORBIDDEN_CODE) {
                throw new UnauthorizedException();
            }
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }

    private void updateData(String URL, Object object) throws HttpException {
        GenericUrl url = new GenericUrl(URL);
        try {
            JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, object);
            HttpMediaType mediaType = new HttpMediaType("application/merge-patch+json");
            mediaType.setCharsetParameter(StandardCharsets.UTF_8);
            content.setMediaType(mediaType);
            HttpRequest req = REQUEST_FACTORY.buildPatchRequest(url, content);
            req.getHeaders().setAuthorization(credentials.getToken());
            HttpResponse resp = req.execute();
            resp.disconnect();
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == UNAUTHORIZED_CODE || ex.getStatusCode() == FORBIDDEN_CODE) {
                throw new UnauthorizedException();
            }
            throw new HttpException(ex);
        } catch (Exception ex) {
            throw new HttpException(ex);
        }
    }

    private static class LoginInfo {
        @Key
        public String id;
        @Key
        public String password;

        public LoginInfo(String id, String password) {
            this.id = id;
            this.password = password;
        }
    }
}
