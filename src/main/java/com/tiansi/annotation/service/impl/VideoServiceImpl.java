package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.ClipsInfo;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.mapper.VideoMapper;
import com.tiansi.annotation.service.ClipsInfoService;
import com.tiansi.annotation.service.ClipsService;
import com.tiansi.annotation.service.VideoService;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
	@Autowired
	private ClipsInfoService clipsInfoService;
	@Autowired
	private ClipsService clipsService;

	@Override
	public List<Video> findAll() {
		return this.list(null);
	}

	@Override
	public List<Video> findUntagged() {
		return this.list(new QueryWrapper<Video>().eq("tagged", 0));
	}

	@Override
	public List<Video> findSomeones(int userId) {
		return this.list(new QueryWrapper<Video>().eq("tagger", userId));
	}

	@Override
	public List<Video> findSomeonesTagged(int userId) {
		return this.list(new QueryWrapper<Video>().eq("tagger", userId).eq("tagged", 2));
	}

	@Override
	public List<Video> findSomeonesTagging(int userId) {
		return this.list(new QueryWrapper<Video>().eq("tagger", userId).eq("tagged", 1));
	}

	@Override
	public String interceptFrame(String videoPath, String outputPath, int start, int end, int step) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture videoCapture = new VideoCapture(videoPath);

		if (videoCapture.isOpened()) {
			int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
			List<Integer> locations = getLocations(start, end, fps, step);
			List<Mat> frames = getSpecificFrames(videoCapture, locations);
			for (int i = 0; i < frames.size(); i++) {
				Imgcodecs.imwrite(outputPath + "\\" + i + ".jpg", frames.get(i));
			}
		}
		videoCapture.release();
		return outputPath;
	}

	@Override
	public void cutVideo(Video video) {
		String basePath = "C:\\Users\\Theo\\Desktop\\imgTest";
		String videoDirPath = basePath + "\\video-" + video.getId();
		int step = 1;

		File videoDir = new File(videoDirPath);
		if (!videoDir.exists()) {
			videoDir.mkdir();
		}
		List<ClipsInfo> clipsInfos = clipsInfoService.getListByVideoId(video.getId());
		clipsInfos.forEach(clipsInfo -> {
			String outputPath = videoDirPath + "\\clip-" +clipsInfo.getStartTime() + "-" + clipsInfo.getEndTime();
			File outputDir = new File(outputPath);
			if (!outputDir.exists()) {
				outputDir.mkdir();
			}
			interceptFrame(video.getAddress(), outputPath, clipsInfo.getStartTime(), clipsInfo.getEndTime(), step);
			String zipFilePath = compress(outputPath, outputPath + ".zip");
			Clips clips = new Clips();
			clips.setAddress(zipFilePath);
			clipsService.save(clips);
		});

	}

	private String compress(String source, String output) {
		File sourceFile = new File(source);
		if (!sourceFile.exists()) {
			throw new RuntimeException(source + "不存在");
		}
		if (!sourceFile.isDirectory()) {
			throw new RuntimeException(source + "不是文件夹");
		}
		File outputFile = new File(output);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			File[] files = sourceFile.listFiles();
			for (File file : files) {
				try {
					BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
					ZipEntry zipEntry = new ZipEntry(sourceFile.getName() + File.separator + file.getName());
					zipOutputStream.putNextEntry(zipEntry);
					int count;
					byte[] buf = new byte[1024];
					while ((count = bufferedInputStream.read(buf)) != -1) {
						zipOutputStream.write(buf, 0, count);
					}
					bufferedInputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			zipOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}



	private List<Mat> getSpecificFrames(VideoCapture videoCapture, List<Integer> locations) {
		List<Mat> frames = new ArrayList<>();
		for (int location : locations) {
			Mat frame = new Mat();
			videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, location);
			videoCapture.read(frame);
			frames.add(frame);
		}
		return frames;
	}

	private List<Integer> getLocations(int start, int end, int fps, int stepSize) {
		List<Integer> locations = new ArrayList<>();
		int startLocation = (start - 1) * fps + 1;
		int endLocation = (end - 1) * fps;
		int location = startLocation;
		while (location <= endLocation) {
			locations.add(location);
			location += stepSize;
		}
		return locations;
	}

}
