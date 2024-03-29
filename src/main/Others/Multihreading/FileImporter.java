package Others.Multihreading;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import DButils.Tables.GaugeMeasurement;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class FileImporter extends Task<Void> {

    public static final String IN_FILE = "in.file";
    public static final String BUNDLES_LABELS = "Bundles.labels";
    public static Task<Void> myTask;
    public static List<String> importErrors = new ArrayList<> ();
    private final GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    private final RiverDBActions riverDBActions = new RiverDBActions ();
    private final Set<String> riverList = new HashSet<> ();
    private List<File> fileList;

    public void setFileList (List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public Void call () {
        myTask = this;
        importErrors.clear ();
        int progress = 1;
        List<File> list = this.fileList;
        for (File file : list) {
            List<GaugeMeasurement> gaugeMeasurementsListFromOneFile = new ArrayList<> ();

            try (Scanner scanner = new Scanner (new FileReader (file))) {
                while (scanner.hasNextLine ()) {
                    String lineToAdd = scanner.nextLine ();
                    GaugeMeasurement gaugeMeasurement = new GaugeMeasurement ();
                    try {
                        gaugeMeasurement.tryGetGaugeMeasurement (lineToAdd.split (","));
                        gaugeMeasurementsListFromOneFile.add (gaugeMeasurement);
                        riverList.add (lineToAdd.split (",")[2].replace ("\"", "").trim ());
                    } catch (Exception e) {
                        importErrors.add (lineToAdd
                                                  + ResourceBundle.getBundle (BUNDLES_LABELS).getString (IN_FILE)
                                                  + file.getName ()
                                                  + "\n");
                    }
                }
                if (isCancelled ()) break;
                saveGaugeList (gaugeMeasurementsListFromOneFile);

                progress++;
                updateProgress (progress, fileList.size ());

            } catch (Exception e) {
                e.printStackTrace ();
                e.getCause ();
            }
        }
        riverDBActions.queryForAllRiverNames ().forEach (riverList::remove);
        riverDBActions.createOrUpdateRiverTable (riverList);

        myTask = null;
        return null;
    }

    private void saveGaugeList (List<GaugeMeasurement> gaugeMeasurementsListFromOneFile) {
        gaugeDBActions.createOrUpdateMeasurementTable (gaugeMeasurementsListFromOneFile);
    }
}
