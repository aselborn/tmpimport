package dao;

import helper.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Updater {
    private final boolean useSQLite = Boolean.parseBoolean(Util.readConfiguration("usesqlite"));
    public Updater(){

    }

    public void CloseAllRuns() throws SQLException {

        String update = "UPDATE runConfig set Enabled = 0;";
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        thisConnection.setAutoCommit(true);
        thisConnection.createStatement().execute(update);


    }

    public void UpdateRunconfig(int stationId, int period) throws SQLException {

        String update = "UPDATE runConfig set Enabled = 1 WHERE StationId = ? AND PeriodId = ?;";
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        thisConnection.setAutoCommit(true);
        PreparedStatement psmt = thisConnection.prepareStatement(update);

        psmt.setInt(1, stationId);
        psmt.setInt(2, period);

        Boolean ok = psmt.execute();

    }
}
