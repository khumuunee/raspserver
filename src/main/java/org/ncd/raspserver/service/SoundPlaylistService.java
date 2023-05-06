package org.ncd.raspserver.service;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ncd.raspserver.entity.Playlist;
import org.ncd.raspserver.entity.PlaylistSound;
import org.ncd.raspserver.entity.RaspberryGroupsPlaylist;
import org.ncd.raspserver.entity.RaspberrysPlaylist;
import org.ncd.raspserver.entity.ScheduledSound;
import org.ncd.raspserver.entity.ScheduledSoundGroup;
import org.ncd.raspserver.model.MyException;
import org.ncd.raspserver.model.Sound;
import org.ncd.raspserver.repository.PlaylistRepo;
import org.ncd.raspserver.repository.PlaylistSoundRepo;
import org.ncd.raspserver.repository.RaspberryGroupScheduledSoundGroupRepo;
import org.ncd.raspserver.repository.RaspberryGroupsPlaylistRepo;
import org.ncd.raspserver.repository.RaspberrysPlaylistRepo;
import org.ncd.raspserver.repository.RaspberrysScheduledSoundGroupRepo;
import org.ncd.raspserver.repository.ScheduledSoundGroupRepo;
import org.ncd.raspserver.repository.ScheduledSoundRepo;
import org.ncd.raspserver.tools.BaseTool;
import org.ncd.raspserver.tools.ResponseTool;
import org.ncd.raspserver.tools.ServiceTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

@Service
public class SoundPlaylistService {

	@Autowired
	private PlaylistRepo playlistRepo;
	
	@Autowired
	private PlaylistSoundRepo playlistSoundRepo;
	
	@Autowired
	private ScheduledSoundGroupRepo scheduledSoundGroupRepo;
	
	@Autowired
	private ScheduledSoundRepo scheduledSoundRepo;
	
	@Autowired
	private RaspberrysPlaylistRepo raspberrysPlaylistRepo;

	@Autowired
	private RaspberryGroupsPlaylistRepo raspberryGroupsPlaylistRepo;
	
	@Autowired
	private RaspberrysScheduledSoundGroupRepo raspberrysScheduledSoundGroupRepo;
	
	@Autowired
	private RaspberryGroupScheduledSoundGroupRepo raspberryGroupScheduledSoundGroupRepo;
	
	public Map<String, Object> uploadSound(MultipartFile multipartFile, String soundType) throws Exception {
		String soundFolderPath = ServiceTool.getSoundFolderPath();
		if(!"Song".equals(soundType))
			soundFolderPath = soundFolderPath + soundType + ServiceTool.getFolderPathSlash();
		File file = new File(soundFolderPath + multipartFile.getOriginalFilename());
		multipartFile.transferTo(file);
		ServiceTool.createActionLog("upload sound");
		return ResponseTool.createRes();
	}

	public Map<String, Object> getAllSounds() throws Exception {
		//Songs
		File folder = new File(ServiceTool.getSoundFolderPath());
		File[] listOfSongs = folder.listFiles();
		//Ad
		folder = new File(ServiceTool.getSoundFolderPath() + "Ad" + ServiceTool.getFolderPathSlash());
		File[] listOfAds = folder.listFiles();
		//ScheduledSound
		folder = new File(ServiceTool.getSoundFolderPath() + "ScheduledSound" + ServiceTool.getFolderPathSlash());
		File[] listOfScheduledSounds = folder.listFiles();
		//Check
		if ((listOfSongs == null || listOfSongs.length == 0) && (listOfAds == null || listOfAds.length == 0) && (listOfScheduledSounds == null || listOfScheduledSounds.length == 0))
			return ResponseTool.createRes(Map.of("soundList", "empty"));
		List<Sound> soundList = new ArrayList<>();
		if(listOfSongs != null && listOfSongs.length > 0)
			soundList.addAll(Arrays.asList(listOfSongs).stream().filter(x -> !"Ad".equals(x.getName()) && !"ScheduledSound".equals(x.getName())).map(x -> new Sound(x.getName(), "Song", false)).collect(Collectors.toList()));
		if(listOfAds != null && listOfAds.length > 0)
			soundList.addAll(Arrays.asList(listOfAds).stream().map(x -> new Sound(x.getName(), "Ad", false)).collect(Collectors.toList()));
		if(listOfScheduledSounds != null && listOfScheduledSounds.length > 0)
			soundList.addAll(Arrays.asList(listOfScheduledSounds).stream().map(x -> new Sound(x.getName(), "ScheduledSound", false)).collect(Collectors.toList()));		
		return ResponseTool.createRes(Map.of("soundList", soundList));
	}

	@Transactional
	public Map<String, Object> deleteSounds(List<Sound> listSound) throws Exception {				
		System.out.println("deleteSounds " + BaseTool.convertDateToString(new Date()));
		if(BaseTool.khoosonJagsaaltEsekh(listSound))
			return ResponseTool.createRes();
		for(Sound sound : listSound) {
			String soundFolderPath = ServiceTool.getSoundFolderPath();
			if("ScheduledSound".equals(sound.getType())) {
				List<ScheduledSound> listScheduledSound = scheduledSoundRepo.findBySoundName(sound.getName());
				if(!BaseTool.khoosonJagsaaltEsekh(listScheduledSound))
					scheduledSoundRepo.deleteAll(listScheduledSound);
				soundFolderPath = soundFolderPath + "ScheduledSound" + ServiceTool.getFolderPathSlash();
			}
			else {
				List<PlaylistSound> listPlaylistSound = playlistSoundRepo.findBySoundNameAndSoundType(sound.getName(), sound.getType());
				if(!BaseTool.khoosonJagsaaltEsekh(listPlaylistSound))
					playlistSoundRepo.deleteAll(listPlaylistSound);
				if("Ad".equals(sound.getType()))
					soundFolderPath = soundFolderPath + "Ad" + ServiceTool.getFolderPathSlash();
			}			
			File file = new File(soundFolderPath + sound.getName());
			file.delete();
		}
		ServiceTool.createActionLog("delete sound: " + listSound.stream().map(x -> x.getName()).toList());
		return ResponseTool.createRes();
	}

	@Transactional
	public Map<String, Object> savePlaylist(String playlistName) throws Exception {
		List<Playlist> allPlaylist = playlistRepo.findAll();		
		if(!BaseTool.khoosonJagsaaltEsekh(allPlaylist) && allPlaylist.stream()
				.filter(x -> BaseTool.jishiltStringTentsuu(BaseTool.khoosonZaigShakhya(x.getName()), 
						BaseTool.khoosonZaigShakhya(playlistName))).count() > 0)
			throw new MyException("This name already exists"); 
		Playlist playlist = new Playlist(ServiceTool.generateId(), playlistName.trim(), 0, 0, new Date(), new Date(), "user", "user");		
		playlistRepo.save(playlist);
		ServiceTool.createActionLog("save playlist: " + playlistName);
		return ResponseTool.createRes();
	}
	
	@Transactional
	public Map<String, Object> updatePlaylist(Map<String, String> param) throws Exception {
		String oldName = param.get("oldName");
		String newName = param.get("newName");
		List<Playlist> allPlaylist = playlistRepo.findAll();
		if(!BaseTool.khoosonJagsaaltEsekh(allPlaylist) && allPlaylist.stream()
				.filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), 
						newName)).count() > 0)
			throw new MyException("This name already exists");
		Playlist oldPlaylist = allPlaylist.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), oldName)).findAny().orElse(null);
		if(oldPlaylist == null)
			throw new Exception("not found playlist by name: " + oldName);
		playlistRepo.delete(oldPlaylist);
		Playlist newPlaylist = (Playlist)oldPlaylist.clone();
		newPlaylist.setName(newName.trim());
		newPlaylist.setUpdatedDate(new Date());
		playlistRepo.save(newPlaylist);
		ServiceTool.createActionLog("update playlist: " + newName);
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadPlaylists() {
		List<Playlist> allPlaylist = playlistRepo.findAll();
		if(BaseTool.khoosonJagsaaltEsekh(allPlaylist))			
			return ResponseTool.createRes();		
//		List<String> playlists = allPlaylist.stream().map(x -> x.getName()).toList();
		return ResponseTool.createRes(Map.of("playlists", allPlaylist));
	}

	@Transactional
	public Map<String, Object> deletePlaylists(List<String> listPlaylistId) {
		if(BaseTool.khoosonJagsaaltEsekh(listPlaylistId))
			return ResponseTool.createRes();
		for(String id : listPlaylistId) {
			playlistRepo.deleteById(id);
			playlistSoundRepo.deleteByPlaylistId(id);
			List<RaspberrysPlaylist> listRaspPlaylist = raspberrysPlaylistRepo.findByPlaylistId(id);
			if(!BaseTool.khoosonJagsaaltEsekh(listRaspPlaylist))
				raspberrysPlaylistRepo.deleteAll(listRaspPlaylist);
			List<RaspberryGroupsPlaylist> listRaspGroupPlaylist = raspberryGroupsPlaylistRepo.findByPlaylistId(id);
			if(!BaseTool.khoosonJagsaaltEsekh(listRaspGroupPlaylist))
				raspberryGroupsPlaylistRepo.deleteAll(listRaspGroupPlaylist);
		}
		ServiceTool.createActionLog("delete playlists: " + listPlaylistId);
		return ResponseTool.createRes();
	}

	@Transactional
	public Map<String, Object> addSoundsToPlaylist(Map<String, Object> param) {
		String playlistId = (String)param.get("playlistId");
		List<Sound> listAddedSound = BaseTool.convertLinkedToList(Sound.class, (List<?>)param.get("addedSoundList"));
		Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
		if(playlist == null)
			throw new MyException("There playlist no longer exists");
		List<PlaylistSound> listPlaylistSound = playlistSoundRepo.findByPlaylistId(playlistId);
		int orderNumber = 0;
		if(!BaseTool.khoosonJagsaaltEsekh(listPlaylistSound)) {
			listPlaylistSound.sort(Comparator.comparing(PlaylistSound::getOrderNumber));
			orderNumber = Collections.max(listPlaylistSound.stream().map(x -> x.getOrderNumber()).collect(Collectors.toList()));
			orderNumber++;
		}
		else
			listPlaylistSound = new ArrayList<>();
		String id = ServiceTool.generateId();
		Date date = new Date();		
		for(Sound addedSound : listAddedSound) {
			PlaylistSound playlistSound = createPlaylistSound(id, playlistId, addedSound.getName(), addedSound.getType(), orderNumber++, date);
			listPlaylistSound.add(playlistSound);
			id = ServiceTool.increaseIdByOne(id);
		}
		playlistSoundRepo.saveAll(listPlaylistSound);	
		ServiceTool.createActionLog("add sounds to playlist");
		return ResponseTool.createRes();
	}
	private PlaylistSound createPlaylistSound(String id, String playlistId, String soundName, String soundType, int orderNumber, Date createdDate) {
		PlaylistSound playlistSound = new PlaylistSound();
		playlistSound.setId(id);
		playlistSound.setPlaylistId(playlistId);
		playlistSound.setSoundName(soundName);
		playlistSound.setSoundType(soundType);
		playlistSound.setOrderNumber(orderNumber++);
		playlistSound.setCreatedDate(createdDate);
		playlistSound.setCreatedUser("user");
		return playlistSound;
	}

	public Map<String, Object> loadPlaylistSounds(String playlistId) {
		List<PlaylistSound> listPlaylistSound = playlistSoundRepo.findByPlaylistId(playlistId);
		if(BaseTool.khoosonJagsaaltEsekh(listPlaylistSound))
			return ResponseTool.createRes();		
		return ResponseTool.createRes(Map.of("listSound", listPlaylistSound));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, Object> removeSoundsFromPlaylist(Map<String, Object> param) {
		String playlistId = (String) param.get("playlistId");
		List<String> listSoundName = (List<String>) param.get("sounds");
		List<PlaylistSound> listPlaylistSound = playlistSoundRepo.findByPlaylistId(playlistId);
		if(BaseTool.khoosonJagsaaltEsekh(listPlaylistSound))
			throw new MyException("PlaylistSound not found with id: " + playlistId);
		List<PlaylistSound> listRemoveSound = new ArrayList<>();
		for(String soundName : listSoundName) {
			List<PlaylistSound> list = listPlaylistSound.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getSoundName(), soundName)).collect(Collectors.toList());
			if(!BaseTool.khoosonJagsaaltEsekh(list)) {
				listPlaylistSound.removeAll(list);
				listRemoveSound.addAll(list);
			}
		}
		playlistSoundRepo.deleteAll(listRemoveSound);
		if(BaseTool.khoosonJagsaaltEsekh(listPlaylistSound))
			return ResponseTool.createRes();
		listPlaylistSound.sort(Comparator.comparing(PlaylistSound::getOrderNumber));
		int number = 0;
		for(PlaylistSound sound : listPlaylistSound)
			sound.setOrderNumber(number++);		
		playlistSoundRepo.saveAll(listPlaylistSound);
		ServiceTool.createActionLog("remove sounds from playlist");
		return ResponseTool.createRes(Map.of("listSound", listPlaylistSound));
	}

	public Map<String, Object> getAllPlaylists() {
		List<Playlist> listAllPlaylist = playlistRepo.findAll();
		if(BaseTool.khoosonJagsaaltEsekh(listAllPlaylist))
			return ResponseTool.createRes(Map.of("playlists", "empty"));
		return ResponseTool.createRes(Map.of("playlists", listAllPlaylist));
	}

	@Transactional
	public Map<String, Object> reorderPlaylistSounds(Map<String, Object> param) throws Exception {
		String playlistId = (String) param.get("playlistId");
		int srcI = (int) param.get("srcI");
		int desI = (int) param.get("desI");
		List<PlaylistSound> listSound = playlistSoundRepo.findByPlaylistId(playlistId);
		if(BaseTool.khoosonJagsaaltEsekh(listSound))
			throw new Exception("PlaylistSound not found with playlistId: " + playlistId);
		listSound.sort(Comparator.comparing(PlaylistSound::getOrderNumber));
		boolean isUp = srcI > desI; 
		int maxI = isUp ? srcI : desI;
		if(listSound.size() <= maxI)
			throw new Exception("index is too high then listSound");
		listSound.get(srcI).setOrderNumber(desI);
		listSound.get(srcI).setStatus(1);
		if(isUp) {
			for(int i = desI; i < srcI; i++) {
				if(srcI == i)
					continue;
				listSound.get(i).setOrderNumber(listSound.get(i).getOrderNumber() + 1);
				listSound.get(i).setStatus(1);
			}
		}
		else {
			for(int i = srcI+1; i < desI+1; i++) {
				listSound.get(i).setOrderNumber(listSound.get(i).getOrderNumber() - 1);
				listSound.get(i).setStatus(1);
			}
		}
		List<PlaylistSound> list = listSound.stream().filter(x -> x.getStatus() == 1).collect(Collectors.toList());
		playlistSoundRepo.saveAll(list);
		ServiceTool.createActionLog("reorder playlist sounds");
		return ResponseTool.createRes(Map.of("listSound", listSound));
	}

	@Transactional
	public Map<String, Object> meshPlaylist(Map<String, Object> param) throws Exception {
		String selectedSongPlaylist = (String) param.get("selectedSongPlaylist");
		String selectedAdPlaylist = (String) param.get("selectedAdPlaylist");
		String playlistId = (String) param.get("playlistId");		
		List<Playlist> listSongPlaylist = playlistRepo.findByName(selectedSongPlaylist);
		if(BaseTool.khoosonJagsaaltEsekh(listSongPlaylist))
			throw new Exception("Playlist not found with name: " + selectedSongPlaylist);
		if(listSongPlaylist.size() > 1)
			throw new Exception("Playlist found more than one with name: " +  selectedSongPlaylist);
		List<Playlist> listAdPlaylist = playlistRepo.findByName(selectedAdPlaylist);
		if(BaseTool.khoosonJagsaaltEsekh(listAdPlaylist))
			throw new Exception("Playlist not found with name: " + selectedAdPlaylist);
		if(listSongPlaylist.size() > 1)
			throw new Exception("Playlist found more than one with name: " + selectedAdPlaylist);
		List<PlaylistSound> listSong = playlistSoundRepo.findByPlaylistId(listSongPlaylist.get(0).getId());
		if(BaseTool.khoosonJagsaaltEsekh(listSong))
			throw new MyException("Playlist have no sounds: " + selectedSongPlaylist);
		List<PlaylistSound> listAd = playlistSoundRepo.findByPlaylistId(listAdPlaylist.get(0).getId());
		if(BaseTool.khoosonJagsaaltEsekh(listAd))
			throw new MyException("Playlist have no sounds: " + selectedAdPlaylist);
		List<PlaylistSound> listMeshSound = new ArrayList<>();
		listSong.sort(Comparator.comparing(PlaylistSound::getOrderNumber));
		listAd.sort(Comparator.comparing(PlaylistSound::getOrderNumber));
		boolean isSongMore = listSong.size() > listAd.size();
		int loopCount = isSongMore ? listSong.size() : listAd.size();
		int j = 0;
		for(int i = 0; i < loopCount; i++) {
			if(isSongMore) {
				listMeshSound.add(listSong.get(i));
				listMeshSound.add(listAd.get(j));
				j++;
				if(j == listAd.size())
					j = 0;
			}
			else {
				listMeshSound.add(listAd.get(i));
				listMeshSound.add(listSong.get(j));
				j++;
				if(j == listSong.size())
					j = 0;
			}
		}
		String id = ServiceTool.generateId();
		Date date = new Date();
		List<PlaylistSound> listPlaylistSound = new ArrayList<>();
		int orderNumber = 0;
		for(PlaylistSound addedSound : listMeshSound) {
			PlaylistSound playlistSound = createPlaylistSound(id, playlistId, addedSound.getSoundName(), addedSound.getSoundType(), orderNumber++, date);
			listPlaylistSound.add(playlistSound);
			id = ServiceTool.increaseIdByOne(id);
		}
		playlistSoundRepo.deleteByPlaylistId(playlistId);
		playlistSoundRepo.saveAll(listPlaylistSound);
		ServiceTool.createActionLog("mesh playlist");
		return ResponseTool.createRes(Map.of("listSound", listPlaylistSound));
	}

	@Transactional
	public Map<String, Object> saveScheduledSoundGroup(String groupName) throws Exception {
		List<ScheduledSoundGroup> allGroup = scheduledSoundGroupRepo.findAll();		
		if(!BaseTool.khoosonJagsaaltEsekh(allGroup) && allGroup.stream()
				.filter(x -> BaseTool.jishiltStringTentsuu(BaseTool.khoosonZaigShakhya(x.getName()), 
						BaseTool.khoosonZaigShakhya(groupName))).count() > 0)
			throw new MyException("This name already exists"); 
		ScheduledSoundGroup playlist = new ScheduledSoundGroup(ServiceTool.generateId(), groupName.trim(), 0, 0, new Date(), new Date(), "user", "user");		
		scheduledSoundGroupRepo.save(playlist);
		ServiceTool.createActionLog("save scheduled sound group");
		return ResponseTool.createRes();
	}
	
	public Map<String, Object> loadScheduledSoundGroups() {
		List<ScheduledSoundGroup> allGroup = scheduledSoundGroupRepo.findAll();
		if(BaseTool.khoosonJagsaaltEsekh(allGroup))			
			return ResponseTool.createRes();		
		return ResponseTool.createRes(Map.of("groups", allGroup));
	}
	
	@Transactional
	public Map<String, Object> updateScheduledSoundGroup(Map<String, String> param) throws Exception {
		String oldName = param.get("oldName");
		String newName = param.get("newName");
		List<ScheduledSoundGroup> allGroup = scheduledSoundGroupRepo.findAll();
		if(!BaseTool.khoosonJagsaaltEsekh(allGroup) && allGroup.stream()
				.filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), 
						newName)).count() > 0)
			throw new MyException("This name already exists");
		ScheduledSoundGroup oldGroup = allGroup.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), oldName)).findAny().orElse(null);
		if(oldGroup == null)
			throw new Exception("not found ScheduledSoundGroup by name: " + oldName);
		scheduledSoundGroupRepo.delete(oldGroup);
		ScheduledSoundGroup newGroup = (ScheduledSoundGroup)oldGroup.clone();
		newGroup.setName(newName.trim());
		newGroup.setUpdatedDate(new Date());
		scheduledSoundGroupRepo.save(newGroup);
		ServiceTool.createActionLog("update scheduled sound group");
		return ResponseTool.createRes();
	}
	
	@Transactional
	public Map<String, Object> deleteScheduledSoundGroups(List<String> listGroupId) {
		if(BaseTool.khoosonJagsaaltEsekh(listGroupId))
			return ResponseTool.createRes();
		for(String id : listGroupId) {
			scheduledSoundGroupRepo.deleteById(id);
			scheduledSoundRepo.deleteByGroupId(id);
			raspberrysScheduledSoundGroupRepo.deleteByGroupId(id);
			raspberryGroupScheduledSoundGroupRepo.deleteByGroupId(id);
		}
		ServiceTool.createActionLog("delete scheduled sound groups");
		return ResponseTool.createRes();
	}
	
	public Map<String, Object> getScheduledSounds() throws Exception {
		//ScheduledSound
		File folder = new File(ServiceTool.getSoundFolderPath() + "ScheduledSound" + ServiceTool.getFolderPathSlash());
		File[] listOfScheduledSounds = folder.listFiles();
		//Check
		if (listOfScheduledSounds == null || listOfScheduledSounds.length == 0)
			return ResponseTool.createRes(Map.of("soundList", "empty"));
		List<Sound> soundList = new ArrayList<>();		
		soundList.addAll(Arrays.asList(listOfScheduledSounds).stream().map(x -> new Sound(x.getName(), "ScheduledSound", false)).collect(Collectors.toList()));		
		return ResponseTool.createRes(Map.of("soundList", soundList));
	}
	
	@Transactional
	public Map<String, Object> addScheduledSoundsToGroup(Map<String, Object> param) {
		String groupId = (String)param.get("groupId");
		List<Sound> listAddedSound = BaseTool.convertLinkedToList(Sound.class, (List<?>)param.get("addedSoundList"));
		int loopCount = (int)param.get("loopCount");
		LocalTime startTime = LocalTime.parse((String)param.get("startTime"));
		ScheduledSoundGroup group = scheduledSoundGroupRepo.findById(groupId).orElse(null);
		if(group == null)
			throw new MyException("There group no longer exists");
		List<ScheduledSound> listSound = new ArrayList<>();
		String id = ServiceTool.generateId();
		Date date = new Date();		
		for(Sound addedSound : listAddedSound) {
			ScheduledSound scheduledSound = createScheduledSound(id, groupId, addedSound.getName(), startTime, loopCount, date);
			listSound.add(scheduledSound);
			id = ServiceTool.increaseIdByOne(id);
		}
		scheduledSoundRepo.saveAll(listSound);	
		ServiceTool.createActionLog("add scheduled sounds to group");
		return ResponseTool.createRes();
	}
	private ScheduledSound createScheduledSound(String id, String groupId, String soundName, LocalTime startTime, int loopCount, Date createdDate) {
		ScheduledSound scheduledSound = new ScheduledSound();
		scheduledSound.setId(id);
		scheduledSound.setGroupId(groupId);
		scheduledSound.setSoundName(soundName);
		scheduledSound.setStartTime(startTime);
		scheduledSound.setLoopCount(loopCount);
		scheduledSound.setCreatedDate(createdDate);
		scheduledSound.setCreatedUser("user");
		return scheduledSound;
	}
	
	public Map<String, Object> loadScheduledSounds(String groupId) {
		List<ScheduledSound> listSound = scheduledSoundRepo.findByGroupId(groupId);
		if(BaseTool.khoosonJagsaaltEsekh(listSound))
			return ResponseTool.createRes();
		return ResponseTool.createRes(Map.of("listSound", listSound));
	}	
	
	@Transactional
	@SuppressWarnings("unchecked")
	public Map<String, Object> removeSoundsFromGroup(Map<String, Object> param) {
		String groupId = (String) param.get("groupId");
		List<String> listSoundName = (List<String>) param.get("sounds");
		List<ScheduledSound> listSound = scheduledSoundRepo.findByGroupId(groupId);
		if(BaseTool.khoosonJagsaaltEsekh(listSound))
			throw new MyException("ScheduledSound not found with id: " + groupId);
		List<ScheduledSound> listRemoveSound = new ArrayList<>();
		for(String soundName : listSoundName) {
			List<ScheduledSound> list = listSound.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getSoundName(), soundName)).collect(Collectors.toList());
			if(!BaseTool.khoosonJagsaaltEsekh(list)) {
				listSound.removeAll(list);
				listRemoveSound.addAll(list);
			}
		}
		scheduledSoundRepo.deleteAll(listRemoveSound);		
		ServiceTool.createActionLog("remove sounds from group");
		return ResponseTool.createRes(Map.of("listSound", listSound));
	}	
	
	@Transactional
	public Map<String, Object> editScheduledSound(Map<String, Object> param) throws Exception {
		ScheduledSound selectedSound = BaseTool.convertMapToObject(ScheduledSound.class, param.get("selectedSound"));
		int loopCount = Integer.parseInt("" + param.get("loopCount"));
		LocalTime startTime = LocalTime.parse((String)param.get("startTime"));
		ScheduledSound selectedSoundFromDb = scheduledSoundRepo.findById(selectedSound.getId()).orElse(null);
		if(selectedSoundFromDb == null)
			throw new Exception("ScheduledSound not found with id: " + selectedSound.getId());
		selectedSoundFromDb.setLoopCount(loopCount);
		selectedSoundFromDb.setStartTime(startTime);
		scheduledSoundRepo.save(selectedSoundFromDb);		
		ServiceTool.createActionLog("edit scheduled sound");
		return ResponseTool.createRes();
	}
	
	public Map<String, Object> getAllScheduledSoundGroups() {
		List<ScheduledSoundGroup> listAllScheduledSoundGroup = scheduledSoundGroupRepo.findAll();
		if(BaseTool.khoosonJagsaaltEsekh(listAllScheduledSoundGroup))
			return ResponseTool.createRes(Map.of("scheduledSoundGroups", "empty"));
		return ResponseTool.createRes(Map.of("scheduledSoundGroups", listAllScheduledSoundGroup));
	}	
	
}
