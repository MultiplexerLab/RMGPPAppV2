package ipa.rmgppapp.helper;

public class Endpoints {
    public static final String BASE_URL_OLD = "https://rmgppapp.com/android_api/";
    // public static final String BASE_URL2 = "https://rmgpp.rmgppapp.com/api/";

    public static final String BASE_URL = "https://beta.rmgppapp.com/api/";
    public static final String CHECK_SUPERVISOR_URL = BASE_URL + "checkSupervisor";
    public static final String POST_ASSIGNED_WORKER_URL = BASE_URL_OLD  + "insertAssignedWorker.php"; //Unexpected response code 500
    public static final String GET_HR_DATA_URL = BASE_URL + "hrData";
    public static final String GET_PLANNING_DATA_URL = BASE_URL + "planningData";
    public static final String GET_OPERATION_DATA_URL = BASE_URL_OLD + "operationData.php";
    public static final String POST_HOURLY_DATA_URL = BASE_URL + "insertHourlyRecord"; //Unexpected response code 500
    public static final String POST_LINE_DATA_URL = BASE_URL + "insertLineData"; //Unexpected response code 500
    public static final String POST_LINE_TARGET_URL = BASE_URL + "insertLineTarget";
    public static final String GET_HOURLY_RECORD_DATA = BASE_URL + "getHourlyData";
    public static final String GET_ASSIGNED_WORKER_URL = BASE_URL + "getAssignedWorkerData";
    public static final String GET_SUMMERY_DATA = BASE_URL + "getSummeryData";
    public static final String POST_NEW_STYLE_URL = BASE_URL + "insertStyle";
    public static final String CHECK_LINE_TARGET_URL = BASE_URL + "checkLineTarget";
    public static final String GET_PROBLEM_DATA_URL = BASE_URL + "getProblems/";
    public static final String GET_ALL_STYLES = BASE_URL + "getAllStyles"; // SELECT DISTINCT StyleNo FROM order_list
    public static final String GET_ALL_STYLES_OB = BASE_URL + "getAllStylesOB";
    public static final String GET_STYLE_DETAILS = BASE_URL + "getStyleDetails";
    public static final String GET_STYLE_DETAILS_FROM_ORDERLIST = BASE_URL + "getStyleDetailsFromOrderList";
    public static final String DELETE_STYLE_URL = BASE_URL + "deleteStyle";
    public static final String DELETE_LINE_DATA_URL = BASE_URL + "deleteLineData";
    public static final String GET_LINE_RECORD = BASE_URL + "getLineData";
    public static final String UPDATE_LINE_DATA_STATUS = BASE_URL + "updateStatus";
    public static final String UPDATE_STYLE_STATUS = BASE_URL + "updateStatusStyle";
    public static final String GET_STYLE_SUMMERY = BASE_URL + "getLineSummary/";
    public static final String GET_BUILDING_DATA = BASE_URL + "getBuildingData/";
    public static final String CHECK_DEVICEID = BASE_URL + "checkValidFactory/";
}
