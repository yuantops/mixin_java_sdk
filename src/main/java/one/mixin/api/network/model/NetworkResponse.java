package one.mixin.api.network.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class NetworkResponse implements Serializable{
    private static final long serialVersionUID = -8657568618081319412L;

    @Expose
    private JsonElement data;

    public NetworkResponse() {
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }
}
