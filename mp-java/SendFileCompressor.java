import java.io.*;
import java.util.zip.*;

public class SendFileCompressor {
    private static final int BUFFER_SIZE = 4096; // Taille du buffer

    private static void printHelp() {
        System.out.println("Usage: java SenFileCompressor [options] [files]");
        System.out.println("Options:");
        System.out.println("  -h\t Permet d'afficher un message d'aide");
        System.out.println("  -c\t Permet de compresser un fichier en .sfc");
        System.out.println("  -d\t Permet de decompresser un fichier compresser en  .sfc");
        System.out.println("  -r\t Permet de spécifier la destination");
        System.out.println("  -v\t Concerne la verbosité du programme.");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length == 0 || args[0].equals("-h")) {
            printHelp();
        } else if (args[0].equals("-c")) {
            boolean verbose = false;
            String outputDir = "";
            boolean forceOutputDirCreation = false;
            String[] inputFiles = args;
            if (args.length < 2) {
                System.out.println("Aucun fichier à compresser n'a été spécifié.");
                System.exit(1);
            }
            String outputFile = "fichier.sfc";
            if (args.length >= 4 && args[args.length - 2].equals("-r")) {
                outputDir = args[args.length - 1];
                if (args[args.length - 3].equals("-f")) {
                    forceOutputDirCreation = true;
                }
                if (!new File(outputDir).exists() && !forceOutputDirCreation) {
                    System.out.println("Erreur : le répertoire de sortie spécifié n'existe pas.");
                    System.exit(1);
                } else if (!new File(outputDir).exists() && forceOutputDirCreation) {
                    new File(outputDir).mkdirs();
                }
            } else if (args.length >= 3) {
                outputDir = ".";
            }
            if (args[args.length - 1].equals("-v")) {
                verbose = true;
            }
            String[] inputFilesArray = new String[inputFiles.length -1];
            System.arraycopy(inputFiles, 1, inputFilesArray, 0, inputFiles.length -1);
          
            try {
                compressFiles(inputFilesArray, outputFile);
                if (verbose) {
                    System.out.println("Fichier compressé créé : " + outputFile);
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la compression des fichiers : " + e.getMessage());
            }
        
        } else if (args[0].equals("-d")) {
            String archivePath = args[1];
            String outputFilename = "./decompress";
            decompressFiles(archivePath, outputFilename);
        } else {
            System.err.println("Option invalide : " + args[0]);
            printHelp();
        }
    }

    // Methode de compression des fichiers
    public static void compressFiles(String[] files, String outputFilename) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            FileOutputStream fileOutputStream = new FileOutputStream(outputFilename);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

            for (String file : files) {
                File currentFile = new File(file);
                FileInputStream fileInputStream = new FileInputStream(currentFile);
                zipOutputStream.putNextEntry(new ZipEntry(currentFile.getName()));
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
            zipOutputStream.close();
            System.out.println("Les fichiers ont été compressés avec succès dans le fichier " + outputFilename);
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la compression des fichiers : " + e.getMessage());
        }
    }

    // Décompression des fichiers
    public static void decompressFiles(String archivePath, String outputPath) throws IOException {
            File archiveFile = new File(archivePath);
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archiveFile))) {
                ZipEntry entry = zis.getNextEntry();
                while (entry != null) {
                    String fileName = entry.getName();
                    File outputFile = new File(outputDir, fileName);
            
                    if (entry.isDirectory()) {
                        outputFile.mkdirs();
                    } else {
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                bos.write(buffer, 0, len);
                            }
                        }
                    }
            
                    entry = zis.getNextEntry();
                }
                System.out.println("Les fichiers ont été décompressés avec succès dans le dossier " + outputPath);
            }
            catch (IOException e) {
                System.out.println("Une erreur est survenue lors de la décompression des fichiers : " + e.getMessage());
            }
        }
}
