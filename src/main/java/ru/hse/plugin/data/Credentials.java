package ru.hse.plugin.data;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;



@State(name = "Credentials", storages = @Storage("rekoder_credentials.xml"))
public class Credentials implements PersistentStateComponent<Credentials> {
    private String login;
    private String token;
    private boolean remember;

    private Credentials() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static Credentials getInstance() {
        return ServiceManager.getService(Credentials.class);
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    @Override
    public Credentials getState() {
        if (!remember) {
            token = "";
        }
        return this;
    }

    @Override
    public void loadState(@NotNull Credentials state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
