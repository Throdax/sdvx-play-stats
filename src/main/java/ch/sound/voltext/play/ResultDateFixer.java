package ch.sound.voltext.play;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ResultDateFixer {

	private static final Path resultsPath = Paths.get("D:\\Tools\\SoundVoltex\\results");

	public static void main(String[] args) throws IOException {

		Files.walk(resultsPath).filter(resultFile -> resultFile.getFileName().toString().startsWith("sdvx") && resultFile.getFileName().toString().endsWith(".png")).forEach(resultsFile -> {
			String fileName = resultsFile.getFileName().toString();
			fileName = fileName.substring(0,fileName.lastIndexOf("."));
			String [] fileParts = fileName.split("_");
			
			LocalDateTime fileDateTime = LocalDateTime.from(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").parse(fileParts[fileParts.length-2]+"_"+fileParts[fileParts.length-1]));
			
			FileTime ft = FileTime.fromMillis(fileDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

			BasicFileAttributeView fileAttr = Files.getFileAttributeView(resultsFile, BasicFileAttributeView.class);
			try {
				System.out.println("["+fileName+"] Setting to time "+fileDateTime);
				fileAttr.setTimes(ft, ft, ft);
			} catch (IOException e) {
				System.err.println("["+fileName+"] Could not set "+fileDateTime+" as new file time!: "+e.getMessage());
			}
		});
		
		Files.walk(resultsPath).filter(resultFile -> resultFile.getFileName().toString().contains("summary")).forEach(resultsFile -> {
			String fileName = resultsFile.getFileName().toString();
			fileName = fileName.substring(0,fileName.lastIndexOf("."));
			String [] fileParts = fileName.split("_");
			
			LocalDateTime fileDateTime = LocalDateTime.from(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").parse(fileParts[0]+"_190000"));
			
			FileTime ft = FileTime.fromMillis(fileDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

			BasicFileAttributeView fileAttr = Files.getFileAttributeView(resultsFile, BasicFileAttributeView.class);
			try {
				System.out.println("["+fileName+"] Setting to time "+fileDateTime);
				fileAttr.setTimes(ft, ft, ft);
			} catch (IOException e) {
				System.err.println("["+fileName+"] Could not set "+fileDateTime+" as new file time!: "+e.getMessage());
			}
		});

	}

}
