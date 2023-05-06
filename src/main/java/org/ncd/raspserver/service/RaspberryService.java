package org.ncd.raspserver.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.ncd.raspserver.entity.ActionLog;
import org.ncd.raspserver.entity.Playlist;
import org.ncd.raspserver.entity.PlaylistSound;
import org.ncd.raspserver.entity.Raspberry;
import org.ncd.raspserver.entity.RaspberryGroup;
import org.ncd.raspserver.entity.RaspberryGroupScheduledSoundGroup;
import org.ncd.raspserver.entity.RaspberryGroupsPlaylist;
import org.ncd.raspserver.entity.RaspberrysPlaylist;
import org.ncd.raspserver.entity.RaspberrysScheduledSoundGroup;
import org.ncd.raspserver.entity.ScheduledSound;
import org.ncd.raspserver.entity.ScheduledSoundGroup;
import org.ncd.raspserver.model.MyException;
import org.ncd.raspserver.repository.ActionLogRepo;
import org.ncd.raspserver.repository.PlaylistRepo;
import org.ncd.raspserver.repository.PlaylistSoundRepo;
import org.ncd.raspserver.repository.RaspberryGroupRepo;
import org.ncd.raspserver.repository.RaspberryGroupScheduledSoundGroupRepo;
import org.ncd.raspserver.repository.RaspberryGroupsPlaylistRepo;
import org.ncd.raspserver.repository.RaspberryRepo;
import org.ncd.raspserver.repository.RaspberrysPlaylistRepo;
import org.ncd.raspserver.repository.RaspberrysScheduledSoundGroupRepo;
import org.ncd.raspserver.repository.ScheduledSoundGroupRepo;
import org.ncd.raspserver.repository.ScheduledSoundRepo;
import org.ncd.raspserver.tools.BaseTool;
import org.ncd.raspserver.tools.Constants;
import org.ncd.raspserver.tools.ResponseTool;
import org.ncd.raspserver.tools.ResponseTool.ResCode;
import org.ncd.raspserver.tools.ServiceTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;

@Service
public class RaspberryService {

	@Autowired
	private RaspberryRepo raspberryRepo;

	@Autowired
	private RaspberrysPlaylistRepo raspberrysPlaylistRepo;

	@Autowired
	private RaspberryGroupsPlaylistRepo raspberryGroupsPlaylistRepo;

	@Autowired
	private PlaylistRepo playlistRepo;

	@Autowired
	private PlaylistSoundRepo playlistSoundRepo;

	@Autowired
	private RaspberrysScheduledSoundGroupRepo raspberrysScheduledSoundGroupRepo;

	@Autowired
	private RaspberryGroupScheduledSoundGroupRepo raspberryGroupScheduledSoundGroupRepo;

	@Autowired
	private ScheduledSoundGroupRepo scheduledSoundGroupRepo;

	@Autowired
	private ScheduledSoundRepo scheduledSoundRepo;

	@Autowired
	private ActionLogRepo actionLogRepo;

	@Autowired
	private RaspberryGroupRepo raspberryGroupRepo;

	@Transactional
	public Map<String, Object> createNewRaspberry(Map<String, Object> param) {
		String name = (String) param.get("name");
		String ipAddress = (String) param.get("ipAddress");
		List<Raspberry> listRasp = raspberryRepo.findAll();
		if (!BaseTool.khoosonJagsaaltEsekh(listRasp)
				&& listRasp.stream().filter(x -> BaseTool.jishiltStringTentsuu(BaseTool.khoosonZaigShakhya(x.getName()), BaseTool.khoosonZaigShakhya(name))).count() > 0)
			throw new MyException("This name already exists");
		Date date = new Date();
		Raspberry raspberry = new Raspberry(ServiceTool.generateId(), name, ipAddress, date, date, "user", "user");
		raspberryRepo.save(raspberry);
		ServiceTool.createActionLog("create new raspberry: " + name);
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadRaspberryList() {
		List<Raspberry> listRasp = raspberryRepo.findAll();
		if (BaseTool.khoosonJagsaaltEsekh(listRasp))
			return ResponseTool.createRes();
		return ResponseTool.createRes(Map.of("listRasp", listRasp));
	}

	public Map<String, Object> loadRaspberryListForDashboard() {
		List<Raspberry> listRasp = raspberryRepo.findAll();
		if (BaseTool.khoosonJagsaaltEsekh(listRasp))
			return ResponseTool.createRes();
		long songCount;
		long adCount;
		for (Raspberry rasp : listRasp) {
			// Song and ad count
			List<RaspberrysPlaylist> listPlaylist = raspberrysPlaylistRepo.findByRaspberryId(rasp.getId());
			if (listPlaylist == null)
				listPlaylist = new ArrayList<>();
			RaspberrysPlaylist raspPlaylist = listPlaylist.stream().filter(x -> x.getIsActive() == 1).findAny().orElse(null);
			if (raspPlaylist == null) {
				if (!BaseTool.khoosonStringEsekh(rasp.getGroupId())) {
					List<RaspberryGroupsPlaylist> listGroupPlaylist = raspberryGroupsPlaylistRepo.findByRaspberryGroupId(rasp.getGroupId());
					if (listGroupPlaylist == null)
						listGroupPlaylist = new ArrayList<>();
					RaspberryGroupsPlaylist raspGroupPlaylist = listGroupPlaylist.stream().filter(x -> x.getIsActive() == 1).findAny().orElse(null);
					if (raspGroupPlaylist == null) {
						songCount = 0;
						adCount = 0;
					} else {
						songCount = playlistSoundRepo.countByPlaylistIdAndSoundType(raspGroupPlaylist.getPlaylistId(), "Song");
						adCount = playlistSoundRepo.countByPlaylistIdAndSoundType(raspGroupPlaylist.getPlaylistId(), "Ad");
					}
				} else {
					songCount = 0;
					adCount = 0;
				}
			} else {
				songCount = playlistSoundRepo.countByPlaylistIdAndSoundType(raspPlaylist.getPlaylistId(), "Song");
				adCount = playlistSoundRepo.countByPlaylistIdAndSoundType(raspPlaylist.getPlaylistId(), "Ad");
			}
			rasp.setSongCount((int) songCount);
			rasp.setAdCount((int) adCount);
			// Scheduled sound count
			List<RaspberrysScheduledSoundGroup> listGroup = raspberrysScheduledSoundGroupRepo.findByRaspberryId(rasp.getId());
			if (BaseTool.khoosonJagsaaltEsekh(listGroup))
				rasp.setScheduledSoundCount(0);
			else {
				long count = 0;
				for (RaspberrysScheduledSoundGroup group : listGroup)
					count += scheduledSoundRepo.countByGroupId(group.getGroupId());
				rasp.setScheduledSoundCount((int) count);
			}
			if(!BaseTool.khoosonStringEsekh(rasp.getGroupId())) {
				List<RaspberryGroupScheduledSoundGroup> listRSGroup = raspberryGroupScheduledSoundGroupRepo.findByRaspberryGroupId(rasp.getGroupId());
				if(!BaseTool.khoosonJagsaaltEsekh(listRSGroup)) {
					long count = 0;
					for (RaspberryGroupScheduledSoundGroup group : listRSGroup)
						count += scheduledSoundRepo.countByGroupId(group.getGroupId());
					rasp.setScheduledSoundCount(rasp.getScheduledSoundCount() +  (int) count);
				}	
			}
			rasp.setCurrentStatus("empty");
		}
		return ResponseTool.createRes(Map.of("listRasp", listRasp));
	}

	@Transactional
	public Map<String, Object> updateRaspberry(Map<String, Object> param) throws Exception {
		String name = (String) param.get("name");
		String ipAddress = (String) param.get("ipAddress");
		String id = (String) param.get("id");
		Raspberry currentRaspberry = raspberryRepo.findById(id).orElse(null);
		if (currentRaspberry == null)
			throw new Exception("not found Raspberry by id: " + id);
		currentRaspberry.setName(name);
		currentRaspberry.setIpAddress(ipAddress);
		currentRaspberry.setUpdatedDate(new Date());
		raspberryRepo.save(currentRaspberry);
		ServiceTool.createActionLog("update raspberry: " + name + " ipAddress: " + ipAddress);
		return ResponseTool.createRes();
	}

	@Transactional
	public Map<String, Object> deleteRaspberry(List<String> listRaspId) {
		if (BaseTool.khoosonJagsaaltEsekh(listRaspId))
			return ResponseTool.createRes();
		for (String id : listRaspId) {
			raspberryRepo.deleteById(id);
		}
		ServiceTool.createActionLog("delete raspberry: " + listRaspId);
		return ResponseTool.createRes();
	}

	public Map<String, Object> checkConnectionRaspberry(List<Raspberry> listRasp) throws Exception {
		BaseTool.logKhevleye("ekhlel");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		List<CompletableFuture<String>> listCompletableFuture = new ArrayList<>();
		for (Raspberry rasp : listRasp) {
			String url = "http://" + rasp.getIpAddress() + raspRestServiceUrl + "checkConnection";
			CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
				try {
					return checkConnnection(url, rasp.getId());
				} catch (Exception e) {
					return rasp.getId() + "_" + ResponseTool.ResCode.Error.toString();
				}
			});
//					.orTimeout(3000, TimeUnit.MILLISECONDS).exceptionally(throwable -> {
//				BaseTool.logKhevleye("Timed out bolloo");
//				return rasp.getId() + "_" + ResponseTool.ResCode.Error.toString();
//		    });
			listCompletableFuture.add(completableFuture);
		}
		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(listCompletableFuture.toArray(new CompletableFuture[listCompletableFuture.size()]));
		combinedFuture.get();
		for (CompletableFuture<String> completableFuture : listCompletableFuture) {
			String res = completableFuture.get();
			String[] split = res.split("_");
			String raspId = split[0];
			String result = split[1];
			if ("Error".equals(result))
				completableFuture.cancel(true);
			Raspberry raspberry = listRasp.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getId(), raspId)).findAny().get();
			raspberry.setCurrentStatus(result);
		}
		BaseTool.logKhevleye("duuslaa");
		return ResponseTool.createRes(Map.of("checkedList", listRasp));
	}

	private String checkConnnection(String url, String raspId) {
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactoryForCheckConnection());
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + raspId, String.class);
			return response.getBody();
		} catch (Exception ex) {
			ex.printStackTrace();
			return raspId + "_" + ResponseTool.ResCode.Error.toString();
		}
	}

	private SimpleClientHttpRequestFactory getClientHttpRequestFactoryForCheckConnection() {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(2000);
		clientHttpRequestFactory.setReadTimeout(2000);
		return clientHttpRequestFactory;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Map<String, Object> addPlaylistToRaspberry(Map<String, Object> param) {
		String raspId = (String) param.get("raspId");
		List<String> listAddedPlaylistId = (List<String>) param.get("addedPlaylistIds");
//		long count = raspberrysPlaylistRepo.countByRaspberryId(raspId);		
		String id = ServiceTool.generateId();
		Date date = new Date();
		List<RaspberrysPlaylist> listRaspberrysPlaylist = new ArrayList<>();
		for (String playlistId : listAddedPlaylistId) {
			RaspberrysPlaylist raspberrysPlaylist = new RaspberrysPlaylist();
			raspberrysPlaylist.setId(id);
			raspberrysPlaylist.setRaspberryId(raspId);
			raspberrysPlaylist.setPlaylistId(playlistId);
			raspberrysPlaylist.setIsActive((short) 0);
			raspberrysPlaylist.setCreatedDate(date);
			raspberrysPlaylist.setCreatedUser("user");
			listRaspberrysPlaylist.add(raspberrysPlaylist);
			id = ServiceTool.increaseIdByOne(id);
		}
//		if (count == 0)
//			listRaspberrysPlaylist.get(0).setIsActive((short) 1);
		raspberrysPlaylistRepo.saveAll(listRaspberrysPlaylist);
		ServiceTool.createActionLog("add playlist to raspberry");
		return ResponseTool.createRes();
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Map<String, Object> addPlaylistToRaspberryGroup(Map<String, Object> param) {
		String raspGroupId = (String) param.get("raspGroupId");
		List<String> listAddedPlaylistId = (List<String>) param.get("addedPlaylistIds");
		long count = raspberryGroupsPlaylistRepo.countByRaspberryGroupId(raspGroupId);
		String id = ServiceTool.generateId();
		Date date = new Date();
		List<RaspberryGroupsPlaylist> listRaspberryGroupsPlaylist = new ArrayList<>();
		for (String playlistId : listAddedPlaylistId) {
			RaspberryGroupsPlaylist raspberrysPlaylist = new RaspberryGroupsPlaylist();
			raspberrysPlaylist.setId(id);
			raspberrysPlaylist.setRaspberryGroupId(raspGroupId);
			raspberrysPlaylist.setPlaylistId(playlistId);
			raspberrysPlaylist.setIsActive((short) 0);
			raspberrysPlaylist.setCreatedDate(date);
			raspberrysPlaylist.setCreatedUser("user");
			listRaspberryGroupsPlaylist.add(raspberrysPlaylist);
			id = ServiceTool.increaseIdByOne(id);
		}
		if (count == 0)
			listRaspberryGroupsPlaylist.get(0).setIsActive((short) 1);
		raspberryGroupsPlaylistRepo.saveAll(listRaspberryGroupsPlaylist);
		ServiceTool.createActionLog("add playlist to raspberry group");
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadRaspberrysPlaylist(String raspId) throws Exception {
		Raspberry rasp = raspberryRepo.findById(raspId).orElse(null);
		if (rasp == null)
			throw new Exception("Raspberry not found with id: " + raspId);
		List<RaspberrysPlaylist> listRaspberrysPlaylist = new ArrayList<>();
		if (!BaseTool.khoosonStringEsekh(rasp.getGroupId())) {
			List<RaspberryGroupsPlaylist> listRaspGroupPlaylist = raspberryGroupsPlaylistRepo.findByRaspberryGroupId(rasp.getGroupId());
			if (!BaseTool.khoosonJagsaaltEsekh(listRaspGroupPlaylist))
				listRaspGroupPlaylist.forEach(x -> listRaspberrysPlaylist.add(new RaspberrysPlaylist(x.getId(), raspId, x.getPlaylistId(), x.getIsActive(), x.getCreatedDate(), x.getCreatedUser(), 1, null)));
		}
		List<RaspberrysPlaylist> lstRaspberrysPlaylist = raspberrysPlaylistRepo.findByRaspberryId(raspId);
		if (!BaseTool.khoosonJagsaaltEsekh(lstRaspberrysPlaylist))
			listRaspberrysPlaylist.addAll(lstRaspberrysPlaylist);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberrysPlaylist))
			return ResponseTool.createRes();
		List<String> listPlaylistId = listRaspberrysPlaylist.stream().map(x -> x.getPlaylistId()).collect(Collectors.toList());
		List<Playlist> listPlaylist = playlistRepo.findAllById(listPlaylistId);
		if (BaseTool.khoosonJagsaaltEsekh(listPlaylist))
			throw new MyException("Not found Playlist with ids: " + listPlaylistId);
		listRaspberrysPlaylist.forEach(x -> {
			Playlist playlist = listPlaylist.stream().filter(s -> BaseTool.jishiltStringTentsuu(s.getId(), x.getPlaylistId())).findAny().orElse(null);
			if (playlist == null)
				throw new MyException("Not found Playlist with id: " + x.getPlaylistId());
			x.setPlaylistName(playlist.getName());
		});
		return ResponseTool.createRes(Map.of("listPlaylist", listRaspberrysPlaylist));
	}

	public Map<String, Object> loadRaspberryGroupsPlaylist(String raspGroupId) {
		List<RaspberryGroupsPlaylist> listRaspberryGroupsPlaylist = raspberryGroupsPlaylistRepo.findByRaspberryGroupId(raspGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberryGroupsPlaylist))
			return ResponseTool.createRes();
		List<String> listPlaylistId = listRaspberryGroupsPlaylist.stream().map(x -> x.getPlaylistId()).collect(Collectors.toList());
		List<Playlist> listPlaylist = playlistRepo.findAllById(listPlaylistId);
		if (BaseTool.khoosonJagsaaltEsekh(listPlaylist))
			throw new MyException("Not found Playlist with ids: " + listPlaylistId);
		listRaspberryGroupsPlaylist.forEach(x -> {
			Playlist playlist = listPlaylist.stream().filter(s -> BaseTool.jishiltStringTentsuu(s.getId(), x.getPlaylistId())).findAny().orElse(null);
			if (playlist == null)
				throw new MyException("Not found Playlist with id: " + x.getPlaylistId());
			x.setPlaylistName(playlist.getName());
		});
		return ResponseTool.createRes(Map.of("listPlaylist", listRaspberryGroupsPlaylist));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, Object> removePlaylistsFromRaspberry(Map<String, Object> param) {
		String raspId = (String) param.get("raspId");
		List<String> listPlaylistId = (List<String>) param.get("playlistIds");
		List<RaspberrysPlaylist> listRaspberrysPlaylist = raspberrysPlaylistRepo.findByRaspberryId(raspId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberrysPlaylist))
			throw new MyException("RaspberrysPlaylist not found with raspId: " + raspId);
		List<RaspberrysPlaylist> listDeletePlaylist = listRaspberrysPlaylist.stream().filter(x -> listPlaylistId.contains(x.getPlaylistId())).collect(Collectors.toList());
		if (BaseTool.khoosonJagsaaltEsekh(listDeletePlaylist))
			throw new MyException("RaspberrysPlaylist not found with playlistId: " + listPlaylistId);
//		List<RaspberrysPlaylist> listOtherPlaylist = listRaspberrysPlaylist.stream().filter(x -> !listPlaylistId.contains(x.getPlaylistId())).collect(Collectors.toList());
//		if (!BaseTool.khoosonJagsaaltEsekh(listOtherPlaylist)
//				&& listOtherPlaylist.stream().filter(x -> x.getIsActive() == 1).count() == 0) {
//			listOtherPlaylist.get(0).setIsActive((short) 1);
//			raspberrysPlaylistRepo.save(listOtherPlaylist.get(0));
//		}
		raspberrysPlaylistRepo.deleteAll(listDeletePlaylist);
		ServiceTool.createActionLog("remove playlists from raspberry");
		return ResponseTool.createRes();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, Object> removePlaylistsFromRaspberryGroup(Map<String, Object> param) {
		String raspGroupId = (String) param.get("raspGroupId");
		List<String> listPlaylistId = (List<String>) param.get("playlistIds");
		List<RaspberryGroupsPlaylist> listRaspberryGroupsPlaylist = raspberryGroupsPlaylistRepo.findByRaspberryGroupId(raspGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberryGroupsPlaylist))
			throw new MyException("RaspberryGroupsPlaylist not found with raspId: " + raspGroupId);
		List<RaspberryGroupsPlaylist> listDeletePlaylist = listRaspberryGroupsPlaylist.stream().filter(x -> listPlaylistId.contains(x.getPlaylistId())).collect(Collectors.toList());
		if (BaseTool.khoosonJagsaaltEsekh(listDeletePlaylist))
			throw new MyException("RaspberrysPlaylist not found with playlistName: " + listPlaylistId);
		List<RaspberryGroupsPlaylist> listOtherPlaylist = listRaspberryGroupsPlaylist.stream().filter(x -> !listPlaylistId.contains(x.getPlaylistId())).collect(Collectors.toList());
		if (!BaseTool.khoosonJagsaaltEsekh(listOtherPlaylist)
				&& listOtherPlaylist.stream().filter(x -> x.getIsActive() == 1).count() == 0) {
			listOtherPlaylist.get(0).setIsActive((short) 1);
			raspberryGroupsPlaylistRepo.save(listOtherPlaylist.get(0));
		}
		raspberryGroupsPlaylistRepo.deleteAll(listDeletePlaylist);
		ServiceTool.createActionLog("remove playlists from raspberry group");
		return ResponseTool.createRes();
	}

	public Map<String, Object> changeActiveStatusOnRaspberrysPlaylist(Map<String, Object> param) {
		String raspberryId = (String) param.get("raspberryId");
		String playlistId = (String) param.get("playlistId");
		List<RaspberrysPlaylist> listRaspberrysPlaylist = raspberrysPlaylistRepo.findByRaspberryId(raspberryId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberrysPlaylist))
			throw new MyException("RaspberrysPlaylist not found with raspId: " + raspberryId);
		listRaspberrysPlaylist.forEach(x -> x.setIsActive((short) 0));
		RaspberrysPlaylist activePlaylist = listRaspberrysPlaylist.stream().filter(x -> BaseTool.jishiltStringTentsuu(playlistId, x.getPlaylistId())).findAny().orElse(null);
		if (activePlaylist == null)
			throw new MyException("RaspberrysPlaylist not found with playlistId: " + playlistId);
		activePlaylist.setIsActive((short) 1);
		raspberrysPlaylistRepo.saveAll(listRaspberrysPlaylist);
		ServiceTool.createActionLog("change active status on raspberrys playlist: " + playlistId);
		return ResponseTool.createRes();
	}

	public Map<String, Object> changeActiveStatusOnRaspberryGroupsPlaylist(Map<String, Object> param) {
		String raspberryGroupId = (String) param.get("raspberryGroupId");
		String playlistId = (String) param.get("playlistId");
		List<RaspberryGroupsPlaylist> listRaspberryGroupsPlaylist = raspberryGroupsPlaylistRepo.findByRaspberryGroupId(raspberryGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberryGroupsPlaylist))
			throw new MyException("RaspberryGroupsPlaylist not found with raspId: " + raspberryGroupId);
		listRaspberryGroupsPlaylist.forEach(x -> x.setIsActive((short) 0));
		RaspberryGroupsPlaylist activePlaylist = listRaspberryGroupsPlaylist.stream().filter(x -> BaseTool.jishiltStringTentsuu(playlistId, x.getPlaylistId())).findAny().orElse(null);
		if (activePlaylist == null)
			throw new MyException("RaspberryGroupsPlaylist not found with playlistId: " + playlistId);
		activePlaylist.setIsActive((short) 1);
		raspberryGroupsPlaylistRepo.saveAll(listRaspberryGroupsPlaylist);
		ServiceTool.createActionLog("change active status on raspberry groups playlist: " + playlistId);
		return ResponseTool.createRes();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> transferPlaylistToRaspberry(Map<String, Object> param) throws Exception {
		List<String> listRaspId = (List<String>) param.get("raspIds");
		List<Raspberry> listRasp = raspberryRepo.findAllById(listRaspId);
		BaseTool.logKhevleye("ekhlel");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		List<CompletableFuture<Map<String, Object>>> listCompletableFuture = new ArrayList<>();
		// ================================== Send table data to rasp
		// =================================================
		for (Raspberry rasp : listRasp) {
			try {
				Map<String, Object> playlistData = getPlaylistData(rasp);
				List<ScheduledSound> listScheduledSound = getScheduledSounds(rasp);
				String url = "http://" + rasp.getIpAddress() + raspRestServiceUrl + "syncRaspberry";
				CompletableFuture<Map<String, Object>> completableFuture = CompletableFuture.supplyAsync(() -> {
					try {
						return transferPlaylistToRasp(url, (Playlist) playlistData.get("playlist"), (List<PlaylistSound>) playlistData.get("listSound"), listScheduledSound, rasp.getId());
					} catch (Exception ex) {
						ex.printStackTrace();
						Map<String, Object> res = new HashMap<>();
						res.put("raspId", rasp.getId());
						res.put(ResponseTool.ResCodeStr, "Error");
						res.put(ResponseTool.ErrorMessageStr, ex.getMessage());
						return res;
					}
				});
				listCompletableFuture.add(completableFuture);
			} catch (Exception ex) {
				ex.printStackTrace();
				rasp.setCurrentStatus(ResCode.Error.toString());
				rasp.setStatusMessage(ex.getMessage());
			}
		}
		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(listCompletableFuture.toArray(new CompletableFuture[listCompletableFuture.size()]));
		combinedFuture.get();
		for (CompletableFuture<Map<String, Object>> completableFuture : listCompletableFuture) {
			Map<String, Object> res = completableFuture.get();
			String raspId = (String) res.get("raspId");
			String result = (String) res.get(ResponseTool.ResCodeStr);
			String statusMessage = (String) res.get(ResponseTool.ErrorMessageStr);
			if (ResCode.Error.toString().equals(result))
				completableFuture.cancel(true);
			Raspberry raspberry = listRasp.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getId(), raspId)).findAny().get();
			raspberry.setCurrentStatus(result);
			raspberry.setStatusMessage(statusMessage);
			if (ResCode.Success.toString().equals(result)) {
				List<String> listDownloadSoundName = (List<String>) res.get("listDownloadSoundName");
				raspberry.setListDownloadSoundName(listDownloadSoundName);
				List<String> listDownloadAdSoundName = (List<String>) res.get("listDownloadAdSoundName");
				raspberry.setListDownloadAdSoundName(listDownloadAdSoundName);
				List<String> listDownloadScheduledSoundName = (List<String>) res.get("listDownloadScheduledSoundName");
				raspberry.setListDownloadScheduledSoundName(listDownloadScheduledSoundName);
				System.out.println("1 " + listDownloadSoundName);
				System.out.println("2 " + listDownloadAdSoundName);
				System.out.println("3 " + listDownloadScheduledSoundName);
			}
		}
		// ================================== Send sound file to rasp
		// =================================================
		listCompletableFuture = new ArrayList<>();
		for (Raspberry rasp : listRasp) {
			if (!BaseTool.jishiltStringTentsuu(ResCode.Success.toString(), rasp.getCurrentStatus())
					|| (BaseTool.khoosonJagsaaltEsekh(rasp.getListDownloadSoundName())
							&& BaseTool.khoosonJagsaaltEsekh(rasp.getListDownloadAdSoundName())
							&& BaseTool.khoosonJagsaaltEsekh(rasp.getListDownloadScheduledSoundName())))
				continue;
			addCompletableFutureToSoundTransfer(rasp, raspRestServiceUrl, listCompletableFuture);
		}
		combinedFuture = CompletableFuture.allOf(listCompletableFuture.toArray(new CompletableFuture[listCompletableFuture.size()]));
		combinedFuture.get();
		for (CompletableFuture<Map<String, Object>> completableFuture : listCompletableFuture) {
			Map<String, Object> res = completableFuture.get();
			String raspId = (String) res.get("raspId");
			String result = (String) res.get(ResponseTool.ResCodeStr);
			String statusMessage = (String) res.get(ResponseTool.ErrorMessageStr);
			if (ResCode.Error.toString().equals(result))
				completableFuture.cancel(true);
			Raspberry raspberry = listRasp.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getId(), raspId)).findAny().get();
			raspberry.setCurrentStatus(result);
			raspberry.setStatusMessage(statusMessage);
		}
		BaseTool.logKhevleye("duuslaa");
		ServiceTool.createActionLog("sync raspberrys");
		return ResponseTool.createRes(Map.of("listRasp", listRasp));
	}

	private void addCompletableFutureToSoundTransfer(Raspberry rasp, String raspRestServiceUrl,
			List<CompletableFuture<Map<String, Object>>> listCompletableFuture) {
		try {
			String url = "http://" + rasp.getIpAddress() + raspRestServiceUrl + "transferSound";
			CompletableFuture<Map<String, Object>> completableFuture = CompletableFuture.supplyAsync(() -> {
				try {
					return transferFileToRasp(rasp, url, rasp.getId());
				} catch (Exception ex) {
					ex.printStackTrace();
					Map<String, Object> res = new HashMap<>();
					res.put("raspId", rasp.getId());
					res.put(ResponseTool.ResCodeStr, "Error");
					res.put(ResponseTool.ErrorMessageStr, ex.getMessage());
					return res;
				}
			});
			listCompletableFuture.add(completableFuture);
		} catch (Exception ex) {
			ex.printStackTrace();
			rasp.setCurrentStatus(ResCode.Error.toString());
			rasp.setStatusMessage(ex.getMessage());
		}
	}

	private List<ScheduledSound> getScheduledSounds(Raspberry rasp) {
		List<RaspberrysScheduledSoundGroup> listGroup = raspberrysScheduledSoundGroupRepo.findByRaspberryId(rasp.getId());
		List<RaspberryGroupScheduledSoundGroup> listGroupInGroup = null;
		if (!BaseTool.khoosonStringEsekh(rasp.getGroupId()))
			listGroupInGroup = raspberryGroupScheduledSoundGroupRepo.findByRaspberryGroupId(rasp.getGroupId());
		List<ScheduledSound> listScheduledSound = new ArrayList<>();
		if (BaseTool.khoosonJagsaaltEsekh(listGroup) && BaseTool.khoosonJagsaaltEsekh(listGroupInGroup))
			return listScheduledSound;
		if (!BaseTool.khoosonJagsaaltEsekh(listGroup)) {
			listGroup.forEach(x -> {
				List<ScheduledSound> sounds = scheduledSoundRepo.findByGroupId(x.getGroupId());
				if (!BaseTool.khoosonJagsaaltEsekh(sounds))
					listScheduledSound.addAll(sounds);
			});
		}
		if (!BaseTool.khoosonJagsaaltEsekh(listGroupInGroup)) {
			listGroupInGroup.forEach(x -> {
				List<ScheduledSound> sounds = scheduledSoundRepo.findByGroupId(x.getGroupId());
				if (!BaseTool.khoosonJagsaaltEsekh(sounds))
					listScheduledSound.addAll(sounds);
			});
		}
		return listScheduledSound;
	}

	private Map<String, Object> getPlaylistData(Raspberry rasp) throws Exception {
		List<RaspberrysPlaylist> listRaspPlaylist = raspberrysPlaylistRepo.findByRaspberryId(rasp.getId());
		String playlistId = null;
		if (BaseTool.khoosonJagsaaltEsekh(listRaspPlaylist)
				|| listRaspPlaylist.stream().filter(x -> x.getIsActive() == 1).count() == 0) {
			if (!BaseTool.khoosonStringEsekh(rasp.getGroupId())) {
				List<RaspberryGroupsPlaylist> listRaspGroupPlaylist = raspberryGroupsPlaylistRepo.findByRaspberryGroupId(rasp.getGroupId());
				RaspberryGroupsPlaylist raspGroupPlaylist = listRaspGroupPlaylist.stream().filter(x -> x.getIsActive() == 1).findAny().orElse(null);
				if (raspGroupPlaylist != null)
					playlistId = raspGroupPlaylist.getPlaylistId();
			}
		} else {
			RaspberrysPlaylist raspPlaylist = listRaspPlaylist.stream().filter(x -> x.getIsActive() == 1).findAny().orElse(null);
			if (raspPlaylist != null)
				playlistId = raspPlaylist.getPlaylistId();
		}
		if (BaseTool.khoosonStringEsekh(playlistId))
			throw new Exception("No playlist configured for Raspberry: " + rasp.getName());
		Playlist playlist = playlistRepo.findById(playlistId).orElse(null);
		if (playlist == null)
			throw new Exception("Not found Playlist with id: " + playlistId);
		List<PlaylistSound> listSound = playlistSoundRepo.findByPlaylistId(playlist.getId());
		if (BaseTool.khoosonJagsaaltEsekh(listSound))
			throw new Exception("Not found PlaylistSound with playlist: " + playlist.getName());
		return Map.of("playlist", playlist, "listSound", listSound);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, Object> transferPlaylistToRasp(String url, Playlist playlist, List<PlaylistSound> listSound,
			List<ScheduledSound> listScheduledSound, String raspId) {
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		Map<String, Object> param = new HashMap<>();
		param.put("playlist", playlist);
		param.put("listSound", listSound);
		param.put("listScheduledSound", listScheduledSound);
		param.put("raspId", raspId);
		HttpEntity<Map> request = new HttpEntity<>(param);
		Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
		return response;
	}

	private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
		return getClientHttpRequestFactory(3000);
	}
	private SimpleClientHttpRequestFactory getClientHttpRequestFactory(int connectTimeout) {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(connectTimeout);
		return clientHttpRequestFactory;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> transferFileToRasp(Raspberry rasp, String url, String raspId) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		List<Integer> seq = new ArrayList<>();
		if (!BaseTool.khoosonJagsaaltEsekh(rasp.getListDownloadSoundName()))
			for (String soundName : rasp.getListDownloadSoundName()) {
				body.add("files", getSoundFileResource(soundName, ""));
				seq.add(1);
			}
		if (!BaseTool.khoosonJagsaaltEsekh(rasp.getListDownloadAdSoundName()))
			for (String soundName : rasp.getListDownloadAdSoundName()) {
				body.add("files", getSoundFileResource(soundName, "Ad" + ServiceTool.getFolderPathSlash()));
				seq.add(2);
			}
		if (!BaseTool.khoosonJagsaaltEsekh(rasp.getListDownloadScheduledSoundName()))
			for (String soundName : rasp.getListDownloadScheduledSoundName()) {
				body.add("files", getSoundFileResource(soundName, "ScheduledSound" + ServiceTool.getFolderPathSlash()));
				seq.add(3);
			}
		body.add("raspId", raspId);
		body.add("sequence", seq);
		System.out.println("4 " + seq);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
		return response;
	}

	private Resource getSoundFileResource(String fileName, String subFolder) throws Exception {
		File file = new File(ServiceTool.getSoundFolderPath() + subFolder + fileName);
		return new FileSystemResource(file);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> startPlaylistOnRaspberry(Map<String, Object> param) throws Exception {
		Raspberry rasp = BaseTool.convertMapToObject(Raspberry.class, param.get("rasp"));
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + rasp.getIpAddress() + raspRestServiceUrl + "startPlaylist";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getCurrentPlayerFromRaspberry(Map<String, Object> param) throws Exception {
		Raspberry rasp = BaseTool.convertMapToObject(Raspberry.class, param.get("rasp"));
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + rasp.getIpAddress() + raspRestServiceUrl + "getCurrentPlayerFromRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		res.put("raspName", rasp.getName());
		res.put("raspIpAddress", rasp.getIpAddress());
		return res;
	}

	public Map<String, Object> getCurrentPlayerStatusFromRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		boolean needSleep = true;
		int connectionTimeOut = needSleep ? 3000 : 1000;
		if (raspIpAddress.contains("SPLITFROMHERE")) {
			needSleep = false;
			raspIpAddress = raspIpAddress.split("SPLITFROMHERE")[0];
		}
		if (needSleep)
			Thread.sleep(500);
		Map<String, Object> res = getCurrentPlayerStatusFromRaspberry(raspIpAddress, connectionTimeOut);
		if (!needSleep)
			return res;
		if (!(boolean) res.get("isPlaying")) {
			if (needSleep)
				Thread.sleep(500);
			res = getCurrentPlayerStatusFromRaspberry(raspIpAddress, connectionTimeOut);
			if (!(boolean) res.get("isPlaying")) {
				if (needSleep)
					Thread.sleep(500);
				res = getCurrentPlayerStatusFromRaspberry(raspIpAddress, connectionTimeOut);
			}
		}
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> getCurrentPlayerStatusFromRaspberry(String raspIpAddress, int connectionTimeOut) throws Exception {
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "getCurrentPlayerStatusFromRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory(connectionTimeOut));
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> setCurrentPlayerProgressInRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		double time = (double) param.get("time");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "setCurrentPlayerProgressInRaspberry/" + time;
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		ServiceTool.createActionLog("set current player progress in raspberry: " + raspIpAddress);
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> pausePlayerInRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "pausePlayerInRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		ServiceTool.createActionLog("pause player in raspberry: " + raspIpAddress);
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> playPlayerInRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "playPlayerInRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		ServiceTool.createActionLog("play player in raspberry: " + raspIpAddress);
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> playNextSoundInRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "playNextSoundInRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		ServiceTool.createActionLog("play next sound in raspberry: " + raspIpAddress);
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> playPreviousSoundInRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "playPreviousSoundInRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		ServiceTool.createActionLog("play previous sound in raspberry: " + raspIpAddress);
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> playSoundWithNameInRaspberry(Map<String, Object> param) throws Exception {
		String raspIpAddress = (String) param.get("raspIpAddress");
		String soundName = (String) param.get("soundName");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "playSoundWithNameInRaspberry/" + soundName;
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
		Map<String, Object> res = response.getBody();
		ServiceTool.createActionLog("play sound with name in raspberry: " + raspIpAddress);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, Object> addScheduledSoundGroupToRaspberry(Map<String, Object> param) {
		String raspId = (String) param.get("raspId");
		List<String> listAddedGroupId = (List<String>) param.get("addedGroupIds");
		String id = ServiceTool.generateId();
		Date date = new Date();
		List<RaspberrysScheduledSoundGroup> listRaspberrysScheduledSoundGroup = new ArrayList<>();
		for (String groupId : listAddedGroupId) {
			RaspberrysScheduledSoundGroup raspberrysScheduledSoundGroup = new RaspberrysScheduledSoundGroup();
			raspberrysScheduledSoundGroup.setId(id);
			raspberrysScheduledSoundGroup.setRaspberryId(raspId);
			raspberrysScheduledSoundGroup.setGroupId(groupId);
			raspberrysScheduledSoundGroup.setCreatedDate(date);
			raspberrysScheduledSoundGroup.setCreatedUser("user");
			listRaspberrysScheduledSoundGroup.add(raspberrysScheduledSoundGroup);
			id = ServiceTool.increaseIdByOne(id);
		}
		raspberrysScheduledSoundGroupRepo.saveAll(listRaspberrysScheduledSoundGroup);
		ServiceTool.createActionLog("add scheduled sound group to raspberry");
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadRaspberrysScheduledSoundGroup(String raspId) throws Exception {
		Raspberry rasp = raspberryRepo.findById(raspId).orElse(null);
		if (rasp == null)
			throw new Exception("Raspberry not found with id: " + raspId);
		List<RaspberrysScheduledSoundGroup> listRaspberrysScheduledSoundGroup = new ArrayList<>();
		List<RaspberrysScheduledSoundGroup> lstRaspberrysScheduledSoundGroup = raspberrysScheduledSoundGroupRepo.findByRaspberryId(raspId);
		if (!BaseTool.khoosonJagsaaltEsekh(lstRaspberrysScheduledSoundGroup))
			listRaspberrysScheduledSoundGroup.addAll(lstRaspberrysScheduledSoundGroup);
		if (!BaseTool.khoosonStringEsekh(rasp.getGroupId())) {
			List<RaspberryGroupScheduledSoundGroup> listRaspberryGroupScheduledSoundGroup = raspberryGroupScheduledSoundGroupRepo.findByRaspberryGroupId(rasp.getGroupId());
			if (!BaseTool.khoosonJagsaaltEsekh(listRaspberryGroupScheduledSoundGroup)) {
				for (RaspberryGroupScheduledSoundGroup group : listRaspberryGroupScheduledSoundGroup) {
					RaspberrysScheduledSoundGroup sgroup = listRaspberrysScheduledSoundGroup.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getGroupId(), group.getGroupId())).findAny().orElse(null);
					if (sgroup != null)
						continue;
					listRaspberrysScheduledSoundGroup.add(new RaspberrysScheduledSoundGroup(group.getId(), raspId, group.getGroupId(), null, null, null, 1));
				}
			}
		}
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberrysScheduledSoundGroup))
			return ResponseTool.createRes();
		List<String> listGroupId = listRaspberrysScheduledSoundGroup.stream().map(x -> x.getGroupId()).collect(Collectors.toList());
		List<ScheduledSoundGroup> listGroup = scheduledSoundGroupRepo.findAllById(listGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listGroup))
			throw new MyException("Not found ScheduledSoundGroup with ids: " + listGroupId);
		listRaspberrysScheduledSoundGroup.forEach(x -> {
			ScheduledSoundGroup group = listGroup.stream().filter(s -> BaseTool.jishiltStringTentsuu(s.getId(), x.getGroupId())).findAny().orElse(null);
			if (group == null)
				throw new MyException("Not found ScheduledSoundGroup with id: " + x.getGroupId());
			x.setGroupName(group.getName());
		});
		return ResponseTool.createRes(Map.of("listGroup", listRaspberrysScheduledSoundGroup));
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Map<String, Object> removeScheduledSoundGroupsFromRaspberry(Map<String, Object> param) {
		String raspId = (String) param.get("raspId");
		List<String> groupIds = (List<String>) param.get("groupIds");
		List<RaspberrysScheduledSoundGroup> listRaspberrysScheduledSoundGroup = raspberrysScheduledSoundGroupRepo.findByRaspberryId(raspId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberrysScheduledSoundGroup))
			throw new MyException("RaspberrysScheduledSoundGroup not found with raspId: " + raspId);
		List<RaspberrysScheduledSoundGroup> listDeleteGroup = listRaspberrysScheduledSoundGroup.stream().filter(x -> groupIds.contains(x.getGroupId())).collect(Collectors.toList());
		if (BaseTool.khoosonJagsaaltEsekh(listDeleteGroup))
			throw new MyException("RaspberrysScheduledSoundGroup not found with groupIds: " + groupIds);
		raspberrysScheduledSoundGroupRepo.deleteAll(listDeleteGroup);
		ServiceTool.createActionLog("remove scheduled sound groups from raspberry");
		return ResponseTool.createRes();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> searchPlayerLogInRaspberry(Map<String, Object> param) throws Exception {
		String searchDateStr = (String) param.get("searchDate");
		String raspIpAddress = (String) param.get("raspIpAddress");
		String raspRestServiceUrl = ServiceTool.getRaspRestServiceUrl();
		String url = "http://" + raspIpAddress + raspRestServiceUrl + "searchPlayerLogInRaspberry";
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		param = new HashMap<>();
		param.put("searchDate", searchDateStr);
		HttpEntity<Map> request = new HttpEntity<>(param);
		Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
		return response;
	}

	public Map<String, Object> searchActionLog(Map<String, Object> param) throws Exception {
		String searchDateStr = (String) param.get("searchDate");
		Date searchDate = BaseTool.convertStringToDate(searchDateStr, Constants.EngiinOgnooniiFormat);
		Date startDate = BaseTool.ognoondTsagOnooy(searchDate, 0, 0, 0);
		Date endDate = BaseTool.ognoondTsagOnooy(searchDate, 23, 59, 59);
		List<ActionLog> listAction = actionLogRepo.findAllByActionDateBetweenOrderByActionDate(startDate, endDate);
		return ResponseTool.createRes(Map.of("listAction", listAction));
	}

	@Transactional
	public Map<String, Object> saveRaspberryGroup(String groupName) throws Exception {
		List<RaspberryGroup> allGroup = raspberryGroupRepo.findAll();
		if (!BaseTool.khoosonJagsaaltEsekh(allGroup)
				&& allGroup.stream().filter(x -> BaseTool.jishiltStringTentsuu(BaseTool.khoosonZaigShakhya(x.getName()), BaseTool.khoosonZaigShakhya(groupName))).count() > 0)
			throw new MyException("This name already exists");
		RaspberryGroup group = new RaspberryGroup(ServiceTool.generateId(), groupName.trim(), 0, new Date(), new Date(), "user", "user");
		raspberryGroupRepo.save(group);
		ServiceTool.createActionLog("save raspberry group");
		return ResponseTool.createRes();
	}

	@Transactional
	public Map<String, Object> updateRaspberryGroup(Map<String, String> param) throws Exception {
		String oldName = param.get("oldName");
		String newName = param.get("newName");
		List<RaspberryGroup> allGroup = raspberryGroupRepo.findAll();
		if (!BaseTool.khoosonJagsaaltEsekh(allGroup)
				&& allGroup.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), newName)).count() > 0)
			throw new MyException("This name already exists");
		RaspberryGroup oldGroup = allGroup.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), oldName)).findAny().orElse(null);
		if (oldGroup == null)
			throw new Exception("not found RaspberryGroup by name: " + oldName);
		raspberryGroupRepo.delete(oldGroup);
		RaspberryGroup newGroup = (RaspberryGroup) oldGroup.clone();
		newGroup.setName(newName.trim());
		newGroup.setUpdatedDate(new Date());
		raspberryGroupRepo.save(newGroup);
		ServiceTool.createActionLog("update raspberry group");
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadRaspberryGroups() {
		List<RaspberryGroup> allGroup = raspberryGroupRepo.findAll();
		if (BaseTool.khoosonJagsaaltEsekh(allGroup))
			return ResponseTool.createRes();
		return ResponseTool.createRes(Map.of("groups", allGroup));
	}

	@Transactional
	public Map<String, Object> deleteRaspberryGroups(List<String> listGroupId) {
		if (BaseTool.khoosonJagsaaltEsekh(listGroupId))
			return ResponseTool.createRes();
		for (String id : listGroupId) {
			raspberryGroupRepo.deleteById(id);
//			scheduledSoundRepo.deleteByGroupId(id);
		}
		ServiceTool.createActionLog("delete raspberry groups");
		return ResponseTool.createRes();
	}

	@Transactional
	public Map<String, Object> addRaspberrysToGroup(Map<String, Object> param) {
		String groupId = (String) param.get("groupId");
		List<Raspberry> listAddedRasp = BaseTool.convertLinkedToList(Raspberry.class, (List<?>) param.get("addedRaspList"));
		RaspberryGroup group = raspberryGroupRepo.findById(groupId).orElse(null);
		if (group == null)
			throw new MyException("There group no longer exists");
		listAddedRasp.forEach(x -> x.setGroupId(groupId));
		raspberryRepo.saveAll(listAddedRasp);
		ServiceTool.createActionLog("add raspberrys to group");
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadRaspberrysInGroup(String groupId) {
		List<Raspberry> listRasp = raspberryRepo.findByGroupId(groupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRasp))
			return ResponseTool.createRes();
		return ResponseTool.createRes(Map.of("listRasp", listRasp));
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Map<String, Object> removeRaspberrysFromGroup(Map<String, Object> param) {
		String groupId = (String) param.get("groupId");
		List<String> listRaspberryName = (List<String>) param.get("raspberryNames");
		List<Raspberry> listRasp = raspberryRepo.findByGroupId(groupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRasp))
			throw new MyException("Raspberry not found with groupId: " + groupId);
		List<Raspberry> listUpdateRasp = new ArrayList<>();
		for (String raspName : listRaspberryName) {
			List<Raspberry> list = listRasp.stream().filter(x -> BaseTool.jishiltStringTentsuu(x.getName(), raspName)).collect(Collectors.toList());
			if (!BaseTool.khoosonJagsaaltEsekh(list)) {
				list.forEach(x -> x.setGroupId(null));
				listUpdateRasp.addAll(list);
			}
		}
		raspberryRepo.saveAll(listUpdateRasp);
		listRasp.removeAll(listUpdateRasp);
		ServiceTool.createActionLog("remove raspberrys from group");
		return ResponseTool.createRes(Map.of("listRasp", listRasp));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, Object> addScheduledSoundGroupToRaspberryGroup(Map<String, Object> param) throws Exception {
		String raspGroupId = (String) param.get("raspGroupId");
		List<String> listAddedGroupId = (List<String>) param.get("addedGroupIds");
		List<RaspberryGroupScheduledSoundGroup> listRaspberryGroupScheduledSoundGroup = new ArrayList<>();
		String id = ServiceTool.generateId();
		Date date = new Date();
		for (String groupId : listAddedGroupId) {
			RaspberryGroupScheduledSoundGroup raspberrysScheduledSoundGroup = new RaspberryGroupScheduledSoundGroup();
			raspberrysScheduledSoundGroup.setId(id);
			raspberrysScheduledSoundGroup.setRaspberryGroupId(raspGroupId);
			raspberrysScheduledSoundGroup.setGroupId(groupId);
			raspberrysScheduledSoundGroup.setCreatedDate(date);
			raspberrysScheduledSoundGroup.setCreatedUser("user");
			listRaspberryGroupScheduledSoundGroup.add(raspberrysScheduledSoundGroup);
			id = ServiceTool.increaseIdByOne(id);
		}
		raspberryGroupScheduledSoundGroupRepo.saveAll(listRaspberryGroupScheduledSoundGroup);
		ServiceTool.createActionLog("add scheduled sound group to raspberry group");
		return ResponseTool.createRes();
	}

	public Map<String, Object> loadRaspberryGroupScheduledSoundGroup(String raspGroupId) {
		List<RaspberryGroupScheduledSoundGroup> listRaspberryGroupScheduledSoundGroup = raspberryGroupScheduledSoundGroupRepo.findByRaspberryGroupId(raspGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberryGroupScheduledSoundGroup))
			return ResponseTool.createRes();
		List<String> listGroupId = listRaspberryGroupScheduledSoundGroup.stream().map(x -> x.getGroupId()).collect(Collectors.toList());
		List<ScheduledSoundGroup> listGroup = scheduledSoundGroupRepo.findAllById(listGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listGroup))
			throw new MyException("Not found ScheduledSoundGroup with ids: " + listGroupId);
		listRaspberryGroupScheduledSoundGroup.forEach(x -> {
			ScheduledSoundGroup group = listGroup.stream().filter(s -> BaseTool.jishiltStringTentsuu(s.getId(), x.getGroupId())).findAny().orElse(null);
			if (group == null)
				throw new MyException("Not found ScheduledSoundGroup with id: " + x.getGroupId());
			x.setGroupName(group.getName());
		});
		return ResponseTool.createRes(Map.of("listGroup", listRaspberryGroupScheduledSoundGroup));
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Map<String, Object> removeScheduledSoundGroupsFromRaspberryGroup(Map<String, Object> param) {
		String raspGroupId = (String) param.get("raspGroupId");
		List<String> groupIds = (List<String>) param.get("groupIds");
		List<RaspberryGroupScheduledSoundGroup> listRaspberryGroupScheduledSoundGroup = raspberryGroupScheduledSoundGroupRepo.findByRaspberryGroupId(raspGroupId);
		if (BaseTool.khoosonJagsaaltEsekh(listRaspberryGroupScheduledSoundGroup))
			throw new MyException("RaspberryGroupScheduledSoundGroup not found with raspId: " + raspGroupId);
		List<RaspberryGroupScheduledSoundGroup> listDeleteGroup = listRaspberryGroupScheduledSoundGroup.stream().filter(x -> groupIds.contains(x.getGroupId())).collect(Collectors.toList());
		if (BaseTool.khoosonJagsaaltEsekh(listDeleteGroup))
			throw new MyException("RaspberryGroupScheduledSoundGroup not found with groupIds: " + groupIds);
		raspberryGroupScheduledSoundGroupRepo.deleteAll(listDeleteGroup);
		ServiceTool.createActionLog("remove scheduled sound groups from raspberry group");
		return ResponseTool.createRes();
	}

}
