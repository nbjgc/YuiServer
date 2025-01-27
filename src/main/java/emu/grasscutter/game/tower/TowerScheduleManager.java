package emu.grasscutter.game.tower;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.def.TowerScheduleData;
import emu.grasscutter.server.game.GameServer;

import java.io.FileReader;
import java.util.List;

public class TowerScheduleManager {
    private final GameServer gameServer;

    public GameServer getGameServer() {
        return gameServer;
    }

    public TowerScheduleManager(GameServer gameServer) {
        this.gameServer = gameServer;
        this.load();
    }

    private TowerScheduleConfig towerScheduleConfig;

    public synchronized void load(){
        try (FileReader fileReader = new FileReader(Grasscutter.getConfig().DATA_FOLDER + "TowerSchedule.json")) {
            towerScheduleConfig = Grasscutter.getGsonFactory().fromJson(fileReader, TowerScheduleConfig.class);

        } catch (Exception e) {
            Grasscutter.getLogger().error("Unable to load tower schedule config.", e);
        }
    }

    public TowerScheduleConfig getTowerScheduleConfig() {
        return towerScheduleConfig;
    }

    public TowerScheduleData getCurrentTowerScheduleData(){
        var data = GameData.getTowerScheduleDataMap().get(towerScheduleConfig.getScheduleId());
        if(data == null){
            Grasscutter.getLogger().error("Could not get current tower schedule data by config:{}", towerScheduleConfig);
        }
        return data;
    }

    public List<Integer> getScheduleFloors() {
        return getCurrentTowerScheduleData().getSchedules().get(0).getFloorList();
    }

    public int getNextFloorId(int floorId){
        var entranceFloors = getCurrentTowerScheduleData().getEntranceFloorId();
        var nextId = 0;
        // find in entrance floors first
        for(int i=0;i<entranceFloors.size()-1;i++){
            if(floorId == entranceFloors.get(i)){
                nextId = entranceFloors.get(i+1);
            }
        }
        if(nextId != 0){
            return nextId;
        }
        var scheduleFloors = getScheduleFloors();
        // find in schedule floors
        for(int i=0;i<scheduleFloors.size()-1;i++){
            if(floorId == scheduleFloors.get(i)){
                nextId = scheduleFloors.get(i+1);
            }
        }
        return nextId;
    }

    public Integer getLastEntranceFloor() {
        return getCurrentTowerScheduleData().getEntranceFloorId().get(getCurrentTowerScheduleData().getEntranceFloorId().size()-1);
    }
}
