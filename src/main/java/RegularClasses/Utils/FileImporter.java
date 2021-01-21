package RegularClasses.Utils;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import DButils.TableDBActions.TownDBActions;
import DButils.Tables.GaugeMeasurement;
import DButils.Tables.River;
import DButils.Tables.Town;
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
    private Set<String> townList = new HashSet<>();
    private Set<String> riverList = new HashSet<>();

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public Void call() {
        myTask = this;
        importErrors.clear();
        int progress = 0;

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

                saveGaugeList(gaugeMeasurementsListFromOneFile);

                progress++;
                updateProgress(progress, fileList.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        riverList.removeAll(riverDBActions.queryForAllRiverNames());
        riverDBActions.createOrUpdateRiverTable(riverList);
        townList.removeAll(townDBActions.queryForAllTownNames());
        townDBActions.createOrUpdateTownListTable(townList);
        myTask = null;
        return null;
    }

    private void saveGaugeList(List<GaugeMeasurement> gaugeMeasurementsListFromOneFile) {
        gaugeDBActions.createOrUpdateMeasurementTable(gaugeMeasurementsListFromOneFile);
    }
}
