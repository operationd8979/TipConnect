package Tip.Connect.model.Live;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class LiveShow {
    private String host;
    private List<String> listWatch;


    public LiveShow(String host){
        this.host = host;
        this.listWatch = new ArrayList<>();
    }

    public String addNewWatcher(String watcherID){
        String hostID = host;
        if(listWatch.size()>0){
            hostID = Iterables.getLast(() -> listWatch.iterator());
        }
        listWatch.add(watcherID);
        return hostID;
    }

    public int removeWatcher(String watcherID){
        int index = listWatch.indexOf(watcherID);
        listWatch.remove(watcherID);
        return index;
    }

}
