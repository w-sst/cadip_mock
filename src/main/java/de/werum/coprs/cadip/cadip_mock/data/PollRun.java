package de.werum.coprs.cadip.cadip_mock.data;

// import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.werum.coprs.cadip.cadip_mock.config.InboxConfiguration;
import de.werum.coprs.cadip.cadip_mock.data.model.File;
import de.werum.coprs.cadip.cadip_mock.data.model.Session;

public class PollRun {

	private static final Logger LOG = LogManager.getLogger(PollRun.class);
	private Pattern pattern;
	DateTimeFormatter dateTimeFormatter;
	private InboxConfiguration config;
	private Storage storage;

	public PollRun(InboxConfiguration config, Storage storage) {
		// 1=Satellit, 3=LocalDateTime, 4=Acquisition Orbit
		this.pattern = Pattern.compile("(.+)_((\\d+)(\\d{6}))");

		this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		this.config = config;
		this.storage = storage;
	}

	public void run() throws IOException {

		Path path = Paths.get(config.getPath());
		Stream<Path> list;
		try {
			list = Files.list(path);
		} catch (IOException e) {
			LOG.error("Error reading out fileSystem", e);
			return;
		}

		try {
			list.forEach(entryPath -> {
				Matcher sessionMatcher = pattern.matcher(entryPath.getFileName().toString());
				if (sessionMatcher.find()) {
					processSession(entryPath, sessionMatcher);
				} else {
					LOG.trace("directory not in pattern: " + entryPath.getFileName());
				}
			});
		} finally {
			list.close();
		}
	}

	private void processSession(Path sessionPath, Matcher sessionMatcher) {
		LOG.trace("Processing Session " + sessionPath.getFileName().toString());
		if (!storage.hasSession(sessionPath.getFileName().toString())) {
			createSession(sessionPath, sessionMatcher);
		}
		Set<File> files = storage.getFileSet(sessionPath.getFileName().toString());
		processFilesOfSession(files, sessionPath);
	}

	private void processFilesOfSession(Set<File> files, Path sessionPath) {
		PathWalker fileWalker = new PathWalker(files);
		try {
			// There can be only one file, for each Channel, that is the finalBlock
			files.forEach(o -> {
				if (o.isFinalBlock()) {
					o.setFinalBlock(false);
				}
			});

			Files.walkFileTree(sessionPath, fileWalker);

			for (long i = 1L; i <= config.getNumChannels(); i++) {
				setFinalFileOfChannel(files, i);
			}
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	// Filters files after their channel and gets the file obj with the highest
	// BlockNumber and sets their finalBlock=true
	private void setFinalFileOfChannel(Set<File> files, long channel) {
		Optional<File> finalFile = files.stream().filter(o -> o.getChannel() == channel)
				.reduce((first, second) -> first.getBlockNumber() > second.getBlockNumber() ? first : second);
		if (finalFile.isPresent()) {
			finalFile.get().setFinalBlock(true);
		}
	}

	// private void test(Set<File> files) {
	// files.stream()
	// .collect(Collectors.groupingBy(File::getChannel))
	// .entrySet().forEach(o -> {
	// Optional<File> finalFile = o.getValue().stream().reduce((first, second) ->
	// first.getBlockNumber() > second.getBlockNumber() ? first : second
	// );
	// if (finalFile.isPresent()) {
	// finalFile.get().setFinalBlock(true);
	// }
	// });
	// }

	private void createSession(Path sessionPath, Matcher sessionMatcher) {
		LOG.trace("creating Session: {}", sessionPath.getFileName());
		storage.createFileSet(sessionPath.getFileName().toString());
		LocalDateTime start = LocalDateTime.parse(sessionMatcher.group(3), dateTimeFormatter);
		LocalDateTime stop = start.plusMinutes(16).plusSeconds(23);

		Session newSession = new Session(UUID.randomUUID(),
				sessionPath.getFileName().toString(),
				config.getNumChannels(),
				LocalDateTime.now(),
				sessionMatcher.group(1),
				config.getStationUnitId(),
				Long.valueOf(sessionMatcher.group(4)),
				config.getAcquisitionId(),
				config.getAntennaId(),
				config.getFrontEndId(),
				config.isRetransfer(),
				config.isAntennaStatusOK(),
				config.isFrontEndStatusOK(),
				start,
				stop,
				start,
				stop,
				config.isDownlinkStatusOK(),
				config.isDeliveryPushOK());
		storage.addSessionToList(newSession);
	}

	private class PathWalker extends SimpleFileVisitor<Path> {

		Pattern pattern;
		Set<File> files;

		public PathWalker(Set<File> files) {
			this.pattern = Pattern.compile("DCS_(\\d{2})_(.+_\\d+)_ch(\\d{1})_DSDB_(\\d{5}).raw");
			this.files = files;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
			if (attr.isRegularFile()) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

				Matcher matcher = pattern.matcher(file.getFileName().toString());
				if (matcher.find()) {
					File newFile = new File(file.toString(),
							UUID.randomUUID(),
							file.getFileName().toString(),
							matcher.group(2),
							Long.parseLong(matcher.group(3)),
							Long.parseLong(matcher.group(4)),
							false,
							LocalDateTime.now(),
							(LocalDateTime) null,
							attr.size(),
							config.isRetransfer());
					files.add(newFile);
				}
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			LOG.error(exc);
			return FileVisitResult.CONTINUE;
		}
	}
}
