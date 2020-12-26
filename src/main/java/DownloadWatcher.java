import dao.Inserter;
import helper.ReadCSV;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class DownloadWatcher {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DownloadWatcher.class);
    private String pathToDirectory = null;

    private WatchService watcher = null;
    private Map<WatchKey,Path> keys=null;
    private Boolean recursive = false;
    private Boolean trace=false;
    private long lastModifiedTime = 0;
    private boolean skip = false;

    public DownloadWatcher(String pathToDirectory, Boolean recursive) throws IOException {
        this.pathToDirectory = pathToDirectory;

        this.watcher=FileSystems.getDefault().newWatchService();
        this.keys=new HashMap<WatchKey, Path>();
        this.recursive=recursive;

        if (recursive) {
            System.out.format("Scanning %s ...\n", pathToDirectory);
            registerFileTree(Paths.get(pathToDirectory));
            System.out.println("Scanning compleated...");
        } else {
            register(Paths.get(pathToDirectory));
        }
        this.trace = true;

    }




    void processEvents(){

        for(;;){
            WatchKey key;
            try {
                key = watcher.take();

            } catch (InterruptedException e) {
                log.error(e.getMessage());
                return;
            }

            Path dir = keys.get(key);


            if (dir == null){
                log.warn("Current key [" + dir.toString() + "] not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents() ){
                WatchEvent.Kind kind = event.kind();



                if (kind == StandardWatchEventKinds.OVERFLOW){
                    continue;
                }

                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);


                if (kind == StandardWatchEventKinds.ENTRY_CREATE){

                    log.info("The file : " + name + " arrived, check if a valid SMHI-temperaturefile?...");

                    File finfo = new File(pathToDirectory.concat("/").concat(name.toString()));

                    if ( name.toString().toLowerCase().endsWith(".csv") && !name.toString().contains("crdownload") ){


                        if (lastModifiedTime == 0) {
                            lastModifiedTime = finfo.lastModified();
                            skip=false;
                        } else {
                            if (finfo.lastModified() == lastModifiedTime){
                                lastModifiedTime=0;
                                skip=true;
                                System.out.println("Skipping this crap!!");
                            }
                        }

                        //IS this a downloaded temperature file?
                        ReadCSV readCSV = new ReadCSV(pathToDirectory.concat("/").concat(name.toString()));

                        try {
                            readCSV.Read();

                            log.info("Could read csv-file, rows = " + readCSV.getTemperatureObject().getTemperatureCSVList().size());
                            log.info("Adding these rows for the station =>, " + readCSV.getTemperatureObject().getStationsNamn());

                            if (!skip){
                                Inserter inserter = new Inserter();
                                inserter.setTemperatureObject(readCSV.getTemperatureObject());
                                inserter.insertData();

                                log.info("File Was read to database.");
                            }

                            //Save to database...


                        } catch (IOException e) {
                            log.error(e.getMessage());
                            System.out.println("Error, still watching directory.");
                        }



                        System.out.println("");
                        System.out.println("Continuing to watch directory => : " + pathToDirectory );


                    } else {
                        log.warn("Incomming file was not ending with 'CSV'");
                    }

                }
                if (kind == StandardWatchEventKinds.ENTRY_DELETE){
                    //log.info("The file : " + name + " was deleted, exiting");
                    continue;
                }
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY){
                    //log.info("The file : " + name + " was modified, exiting...");
                    continue;
                }



                if (recursive && (kind == StandardWatchEventKinds.ENTRY_CREATE)){
                    try{
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)){
                            registerFileTree(child);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }

            }

            boolean valid = key.reset();
            if (!valid){
                keys.remove(key);
            }

            if (keys.isEmpty()){
                break;
            }

        }
    }


    @SuppressWarnings("unchecked")
    static<T>WatchEvent<T> cast(WatchEvent<?> event){
        return (WatchEvent<T>)event;
    }

    //Register actual directory with watchService.
    private void register(Path directory) throws IOException{
        WatchKey key = directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

        if (trace){
            Path prev = keys.get(key);
            if (null == prev){
                log.info("register: %s\n", directory);
            } else{
                if (!directory.equals(prev)){
                    log.info("updating: %s >\n", directory);
                }
            }
        }
        keys.put(key, directory);

    }

    //loops through dirs...
    private void registerFileTree(final Path directoryStart) throws IOException {

        Files.walkFileTree(directoryStart, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException{

                register(path);
                return FileVisitResult.CONTINUE;
            }


        });

    }



    private Path createDirectoryIfNotExists() {
        Path path = Paths.get(pathToDirectory);

        if (!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        }

        return path;
    }

}
