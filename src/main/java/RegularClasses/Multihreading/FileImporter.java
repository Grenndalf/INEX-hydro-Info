package RegularClasses.Multihreading;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import DButils.TableDBActions.TownDBActions;
import DButils.Tables.GaugeMeasurement;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class FileImporter extends Task {

    public static final String IN_FILE = "in.file";
    public static final String BUNDLES_LABELS = "Bundles.labels";
    public static Task myTask;
    public static List<String> importErrors = new ArrayList<>();
    private final GaugeDBActions gaugeDBActions = new GaugeDBActions();
    private final TownDBActions townDBActions = new TownDBActions();
    private final RiverDBActions riverDBActions = new RiverDBActions();
    private List<File> fileList;

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    protected synchronized Void call() {
        myTask = this;
        importErrors.clear();
        int progress = 0;
        Set<String> townList = new HashSet<>();
        Set<String> riverList = new HashSet<>();
        for (File file : this.fileList
        ) {
            List<GaugeMeasurement> gaugeMeasurementsListFromOneFile = new ArrayList<>();

            try {
                Scanner scanner = new Scanner(new FileReader(file));
                while (scanner.hasNextLine()) {
                    String lineToAdd = scanner.nextLine();
                    GaugeMeasurement gaugeMeasurement = new GaugeMeasurement();
                    try {
                        gaugeMeasurement.tryGetGaugeMeasurement(lineToAdd.split(","));
                        gaugeMeasurementsListFromOneFile.add(gaugeMeasurement);
                        townList.add(lineToAdd.split(",")[1].replace("\"", "").trim());
                        riverList.add(lineToAdd.split(",")[2].replace("\"", "").trim());
                    } catch (Exception e) {
                        importErrors.add(lineToAdd
                                + ResourceBundle.getBundle(BUNDLES_LABELS).getString(IN_FILE)
                                + file.getName()
                                + "\n");
                    }
                }

                if (isCancelled()) break;
 
                gaugeDBActions.createOrUpdateMeasurementTable(gaugeMeasurementsListFromOneFile);

                progress++;
                updateProgress(progress, this.fileList.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        riverList.removeAll(riverDBActions.queryForAllRiverNames());
        townList.removeAll(townDBActions.queryForAllTownNames());
        riverDBActions.createOrUpdateRiverTable(riverList);
        townDBActions.createOrUpdateTownListTable(townList);
        myTask = null;
        return null;
    }
}
