package org.ncd.raspserver.controller;

import java.util.List;
import java.util.Map;

import org.ncd.raspserver.entity.Raspberry;
import org.ncd.raspserver.model.Sound;
import org.ncd.raspserver.service.RaspberryService;
import org.ncd.raspserver.service.SoundPlaylistService;
import org.ncd.raspserver.tools.ResponseTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class RaspServerController {
	
	@Autowired
	private SoundPlaylistService soundPlaylistService;
	
	@Autowired
	private RaspberryService raspberryService;
	
	@PostMapping("uploadSound")
	public Map<String, Object> uploadSound(@RequestParam("file") MultipartFile file, @RequestParam("soundType") String soundType) {
		try {
			return soundPlaylistService.uploadSound(file, soundType);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("getAllSounds")
	public Map<String, Object> getAllSounds() {
		try {
			return soundPlaylistService.getAllSounds();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("deleteSounds")
	public Map<String, Object> deleteSounds(@RequestBody List<Sound> selectedSounds) {
		try {
			return soundPlaylistService.deleteSounds(selectedSounds);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("savePlaylist")
	public Map<String, Object> savePlaylist(@RequestBody Map<String, String> param) {
		try {
			return soundPlaylistService.savePlaylist(param.get("playlistName"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("updatePlaylist")
	public Map<String, Object> updatePlaylist(@RequestBody Map<String, String> param) {
		try {
			return soundPlaylistService.updatePlaylist(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadPlaylists")
	public Map<String, Object> loadPlaylists() {
		try {
			return soundPlaylistService.loadPlaylists();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("deletePlaylists")
	public Map<String, Object> deletePlaylists(@RequestBody List<String> listPlaylistId) {
		try {
			return soundPlaylistService.deletePlaylists(listPlaylistId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("addSoundsToPlaylist")
	public Map<String, Object> addSoundsToPlaylist(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.addSoundsToPlaylist(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadPlaylistSounds/{playlistId}")
	public Map<String, Object> loadPlaylistSounds(@PathVariable String playlistId) {
		try {
			return soundPlaylistService.loadPlaylistSounds(playlistId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("removeSoundsFromPlaylist")
	public Map<String, Object> removeSoundsFromPlaylist(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.removeSoundsFromPlaylist(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("createNewRaspberry")
	public Map<String, Object> createNewRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.createNewRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberryList")
	public Map<String, Object> loadRaspberryList() {
		try {
			return raspberryService.loadRaspberryList();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberryListForDashboard")
	public Map<String, Object> loadRaspberryListForDashboard() {
		try {
			return raspberryService.loadRaspberryListForDashboard();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("updateRaspberry")
	public Map<String, Object> updateRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.updateRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("deleteRaspberry")
	public Map<String, Object> deleteRaspberry(@RequestBody List<String> listRaspId) {
		try {
			return raspberryService.deleteRaspberry(listRaspId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("checkConnectionRaspberry")
	public Map<String, Object> checkConnectionRaspberry(@RequestBody List<Raspberry> listRaspId) {
		try {
			return raspberryService.checkConnectionRaspberry(listRaspId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("getAllPlaylists")
	public Map<String, Object> getAllPlaylists() {
		try {
			return soundPlaylistService.getAllPlaylists();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("addPlaylistToRaspberry")
	public Map<String, Object> addPlaylistToRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.addPlaylistToRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("addPlaylistToRaspberryGroup")
	public Map<String, Object> addPlaylistToRaspberryGroup(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.addPlaylistToRaspberryGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberrysPlaylist/{raspId}")
	public Map<String, Object> loadRaspberrysPlaylist(@PathVariable String raspId) {
		try {
			return raspberryService.loadRaspberrysPlaylist(raspId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberryGroupsPlaylist/{raspGroupId}")
	public Map<String, Object> loadRaspberryGroupsPlaylist(@PathVariable String raspGroupId) {
		try {
			return raspberryService.loadRaspberryGroupsPlaylist(raspGroupId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("removePlaylistsFromRaspberry")
	public Map<String, Object> removePlaylistsFromRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.removePlaylistsFromRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("removePlaylistsFromRaspberryGroup")
	public Map<String, Object> removePlaylistsFromRaspberryGroup(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.removePlaylistsFromRaspberryGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("changeActiveStatusOnRaspberrysPlaylist")
	public Map<String, Object> changeActiveStatusOnRaspberrysPlaylist(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.changeActiveStatusOnRaspberrysPlaylist(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("changeActiveStatusOnRaspberryGroupsPlaylist")
	public Map<String, Object> changeActiveStatusOnRaspberryGroupsPlaylist(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.changeActiveStatusOnRaspberryGroupsPlaylist(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("transferPlaylistToRaspberry")
	public Map<String, Object> transferPlaylistToRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.transferPlaylistToRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("startPlaylistOnRaspberry")
	public Map<String, Object> startPlaylistOnRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.startPlaylistOnRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("getCurrentPlayerFromRaspberry")
	public Map<String, Object> getCurrentPlayerFromRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.getCurrentPlayerFromRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("getCurrentPlayerStatusFromRaspberry")
	public Map<String, Object> getCurrentPlayerStatusFromRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.getCurrentPlayerStatusFromRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("setCurrentPlayerProgressInRaspberry")
	public Map<String, Object> setCurrentPlayerProgressInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.setCurrentPlayerProgressInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("pausePlayerInRaspberry")
	public Map<String, Object> pausePlayerInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.pausePlayerInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("playPlayerInRaspberry")
	public Map<String, Object> playPlayerInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.playPlayerInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("playNextSoundInRaspberry")
	public Map<String, Object> playNextSoundInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.playNextSoundInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("playPreviousSoundInRaspberry")
	public Map<String, Object> playPreviousSoundInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.playPreviousSoundInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("playSoundWithNameInRaspberry")
	public Map<String, Object> playSoundWithNameInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.playSoundWithNameInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("reorderPlaylistSounds")
	public Map<String, Object> reorderPlaylistSounds(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.reorderPlaylistSounds(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("meshPlaylist")
	public Map<String, Object> meshPlaylist(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.meshPlaylist(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	
	@PostMapping("saveScheduledSoundGroup")
	public Map<String, Object> saveScheduledSoundGroup(@RequestBody Map<String, String> param) {
		try {
			return soundPlaylistService.saveScheduledSoundGroup(param.get("groupName"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("updateScheduledSoundGroup")
	public Map<String, Object> updateScheduledSoundGroup(@RequestBody Map<String, String> param) {
		try {
			return soundPlaylistService.updateScheduledSoundGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadScheduledSoundGroups")
	public Map<String, Object> loadScheduledSoundGroups() {
		try {
			return soundPlaylistService.loadScheduledSoundGroups();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("deleteScheduledSoundGroups")
	public Map<String, Object> deleteScheduledSoundGroups(@RequestBody List<String> listGroupId) {
		try {
			return soundPlaylistService.deleteScheduledSoundGroups(listGroupId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("getScheduledSounds")
	public Map<String, Object> getScheduledSounds() {
		try {
			return soundPlaylistService.getScheduledSounds();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("addScheduledSoundsToGroup")
	public Map<String, Object> addScheduledSoundsToGroup(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.addScheduledSoundsToGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadScheduledSounds/{groupId}")
	public Map<String, Object> loadScheduledSounds(@PathVariable String groupId) {
		try {
			return soundPlaylistService.loadScheduledSounds(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("removeSoundsFromGroup")
	public Map<String, Object> removeSoundsFromGroup(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.removeSoundsFromGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("editScheduledSound")
	public Map<String, Object> editScheduledSound(@RequestBody Map<String, Object> param) {
		try {
			return soundPlaylistService.editScheduledSound(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("getAllScheduledSoundGroups")
	public Map<String, Object> getAllScheduledSoundGroups() {
		try {
			return soundPlaylistService.getAllScheduledSoundGroups();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("addScheduledSoundGroupToRaspberry")
	public Map<String, Object> addScheduledSoundGroupToRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.addScheduledSoundGroupToRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberrysScheduledSoundGroup/{raspId}")
	public Map<String, Object> loadRaspberrysScheduledSoundGroup(@PathVariable String raspId) {
		try {
			return raspberryService.loadRaspberrysScheduledSoundGroup(raspId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("removeScheduledSoundGroupsFromRaspberry")
	public Map<String, Object> removeScheduledSoundGroupsFromRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.removeScheduledSoundGroupsFromRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("searchPlayerLogInRaspberry")
	public Map<String, Object> searchPlayerLogInRaspberry(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.searchPlayerLogInRaspberry(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("searchActionLog")
	public Map<String, Object> searchActionLog(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.searchActionLog(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("saveRaspberryGroup")
	public Map<String, Object> saveRaspberryGroup(@RequestBody Map<String, String> param) {
		try {
			return raspberryService.saveRaspberryGroup(param.get("groupName"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("updateRaspberryGroup")
	public Map<String, Object> updateRaspberryGroup(@RequestBody Map<String, String> param) {
		try {
			return raspberryService.updateRaspberryGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberryGroups")
	public Map<String, Object> loadRaspberryGroups() {
		try {
			return raspberryService.loadRaspberryGroups();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("deleteRaspberryGroups")
	public Map<String, Object> deleteRaspberryGroups(@RequestBody List<String> listGroupId) {
		try {
			return raspberryService.deleteRaspberryGroups(listGroupId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("getAllRaspberrys")
	public Map<String, Object> getAllRaspberrys() {
		try {
			return raspberryService.loadRaspberryList();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("addRaspberrysToGroup")
	public Map<String, Object> addRaspberrysToGroup(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.addRaspberrysToGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@GetMapping("loadRaspberrysInGroup/{groupId}")
	public Map<String, Object> loadRaspberrysInGroup(@PathVariable String groupId) {
		try {
			return raspberryService.loadRaspberrysInGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}
	
	@PostMapping("removeRaspberrysFromGroup")
	public Map<String, Object> removeRaspberrysFromGroup(@RequestBody Map<String, Object> param) {
		try {
			return raspberryService.removeRaspberrysFromGroup(param);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseTool.createResWithError(e);
		}
	}

}
