package ru.hse.plugin.managers;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import ru.hse.plugin.data.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

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
        return "token";
    }

    public List<Team> getTeams() {
        List<Team> list = new ArrayList<>();
        User user = getUser();
        for (String teamName : user.getTeams()) {
            list.add(getTeam(teamName));
        }
        return list;
    }

    public Team getTeam(String teamName) {
        return getData(TEAMS_URL + teamName, Team.class);
    }

    public User getUser() {
        return getData(USER_URL, User.class);
    }

    public List<Folder> getRootFolders(Team team) { // TODO
        Folder[] folders = getData(TEAM_FOLDERS_URL.replace(REPLACEMENT, team.getName()), Folder[].class);
        return Arrays.asList(folders);
    }

    public List<Folder> getPersonalRootFolders() {
        List<Folder> list = new ArrayList<>();
        list.add(getFolderWithAllProblems());
        Folder[] folders = getData(USER_FOLDERS_URL, Folder[].class);
        list.addAll(Arrays.asList(folders));
        return list;
    }

    private Folder getFolderWithAllProblems() {
        Folder all = new Folder("All", -1);
        all.setLoaded();
        getAllProblems().stream().peek(Problem::setLoaded).forEach(all::add);
        return all;
    }

    private List<Problem> getAllProblems() {
        Problem[] problems = getData(USER_PROBLEMS_URL, Problem[].class);
        return Arrays.asList(problems);
    }

    public List<TreeFile> loadFolder(String folderId) {
        List<TreeFile> files = new ArrayList<>();
        files.addAll(getFolderSubFolders(folderId));
        files.addAll(getFolderProblems(folderId));
        return files;
    }

    private List<Folder> getFolderSubFolders(String folderId) {
        Folder[] folders = getData(FOLDER_SUB_FOLDERS_URL.replace(REPLACEMENT, folderId), Folder[].class);
        return Arrays.asList(folders);
    }

    private List<Problem> getFolderProblems(String folderId) {
        Problem[] problems = getData(FOLDER_PROBLEMS_URL.replace(REPLACEMENT, folderId), Problem[].class);
        return Arrays.asList(problems);
    }

    public Problem loadProblem(String problemId) {
        return getData(PROBLEMS_URL + problemId, Problem.class);
    }

    public Submission loadSubmission(String submissionId) {
        return getData(SUBMISSIONS_URL + submissionId, Submission.class);
    }

    public List<Submission> getProblemSubmissions(Problem problem) {
        Submission[] submissions = getData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), Submission[].class);
        return Arrays.asList(submissions);
    }

    public void sendSubmission(Problem problem, Submission submission) {
        sendData(PROBLEM_SUBMISSIONS_URL.replace(REPLACEMENT, String.valueOf(problem.getId())), submission);
    }

    private <T> T getData(String URL, Class<T> tClass) {
        GenericUrl url = new GenericUrl(URL);
        try {
            HttpRequest req = REQUEST_FACTORY.buildGetRequest(url);
            req.setParser(new JsonObjectParser(JSON_FACTORY));
            HttpResponse resp = req.execute();
            //TODO: проверять status code
            return resp.parseAs(tClass);
        } catch (IOException e) {
            //TODO: нормальные исключения
            throw new RuntimeException(e);
        }
    }

    private void sendData(String URL, Object object) {
        GenericUrl url = new GenericUrl(URL);
        try {
            HttpRequest req = REQUEST_FACTORY.buildPostRequest(url, new JsonHttpContent(JSON_FACTORY, object));
            HttpResponse resp = req.execute();
            //TODO: проверять status code
        } catch (IOException e) {
            //TODO: нормальные исключения
            throw new RuntimeException(e);
        }
    }

    private static final Random rnd = new Random();
    private static final String ALL_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static String getRandomString(int len) {
        char[] buffer = new char[len];
        for (int k = 0; k < buffer.length; k++) {
            buffer[k] = ALL_CHARS.charAt(rnd.nextInt(ALL_CHARS.length()));
        }
        return String.valueOf(buffer);
    }
}
