package com.pkit.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.SparseArray;

import com.pkit.launcher.bean.Address;
import com.pkit.launcher.bean.DeviceConfig;
import com.pkit.launcher.bean.PageInfo;
import com.pkit.launcher.bean.Upgrade;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.ModelSeat;
import com.pkit.launcher.service.aidl.Source;
import com.pkit.launcher.service.aidl.Tag;
import com.pkit.launcher.service.aidl.TagGroup;
import com.pkit.launcher.utils.APPLog;

/**
 * Created by jiaxing on 2015/6/17.
 */
public class ParserUtils {

	public static SparseArray<ArrayList<Content>> parseHomeData(String data) throws JSONException {
		SparseArray<ArrayList<Content>> homeData = new SparseArray<ArrayList<Content>>();
		JSONObject jsonObj = new JSONObject(data);
		int code = jsonObj.getInt("code");
		if (code != 1) {
			return null;
		}
		JSONArray jsonArray = jsonObj.getJSONArray("entity");
		int len = jsonArray.length();
		for (int i = 0; i < len; i++) {
			JSONObject modelJson = jsonArray.getJSONObject(i);
			JSONArray modelSeatsJsonArray = modelJson.getJSONArray("modelSeats");
			ArrayList<Content> modelSeats = parseModelSeats(modelSeatsJsonArray);
			homeData.put(i, modelSeats);
		}
		return homeData;
	}

	private static ArrayList<Content> parseModelSeats(JSONArray modelSeatsJsonArray) throws JSONException {
		ArrayList<Content> modelSeats = new ArrayList<Content>();
		int len = modelSeatsJsonArray.length();
		for (int i = 0; i < len; i++) {
			JSONObject modelSeatJson = modelSeatsJsonArray.getJSONObject(i);
			String id = modelSeatJson.getString("id");
			int sequence = modelSeatJson.getInt("sequence");
			String name = modelSeatJson.getString("name");
			String thumbnail = modelSeatJson.getString("thumbnail");
			String poster = modelSeatJson.getString("poster");
			String stills = modelSeatJson.getString("stills");
			int action = modelSeatJson.getInt("action");
			String actionValue = modelSeatJson.getString("actionValue");
			String pckageName = modelSeatJson.getString("pckageName");
			String classNamePackage = modelSeatJson.getString("classNamePackage");
			String pram = modelSeatJson.getString("pram");

			ModelSeat modelSeat = new ModelSeat();
			modelSeat.setId(id);
			modelSeat.setSequence(sequence);
			modelSeat.setName(name);
			modelSeat.setThumbnail(thumbnail);
			modelSeat.setPoster(poster);
			modelSeat.setStills(stills);
			modelSeat.setAction(action);
			modelSeat.setActionValue(actionValue);
			modelSeat.setPackageName(pckageName);
			modelSeat.setClassNamePackage(classNamePackage);
			modelSeat.setPram(pram);

			modelSeats.add(modelSeat);
		}
		return modelSeats;
	}

	public static PageInfo convertDataToPageInfo(String data) {
		PageInfo pageInfo = new PageInfo();
		JsonReader reader = new JsonReader(new StringReader(data));
		try {
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String key = reader.nextName();
				if ("entity".equals(key)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String key1 = reader.nextName();
						if ("pageInfo".equals(key1)) {
							reader.beginObject();
							while (reader.hasNext()) {
								String key2 = reader.nextName();
								if ("pageNumber".equals(key2)) {
									pageInfo.setPageNumber(reader.nextInt());
								} else if ("pageSize".equals(key2)) {
									pageInfo.setPageSize(reader.nextInt());
								} else if ("totalPage".equals(key2)) {
									pageInfo.setTotalPage(reader.nextInt());
								} else if ("totalNumber".equals(key2)) {
									pageInfo.setTotalNumber(reader.nextInt());
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();

						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pageInfo;
	}

	public static List<Content> converDataToMenu(String data) {
		List<Content> menus = new ArrayList<Content>();
		JsonReader reader = new JsonReader(new StringReader(data));
		try {
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String key = reader.nextName();
				if ("entity".equals(key)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String key1 = reader.nextName();
						if ("listInfo".equals(key1)) {
							reader.beginArray();
							while (reader.hasNext()) {
								reader.beginObject();
								Container menu = new Container();
								while (reader.hasNext()) {
									String key2 = reader.nextName();
									if ("id".equals(key2)) {
										menu.contentID = reader.nextString();
									} else if ("parentid".equals(key2)) {
										menu.parentID = reader.nextString();
									} else if ("name".equals(key2)) {
										menu.name = reader.nextString();
									} else if ("thumbnail".equals(key2)) {
										menu.imgPaths.add(reader.nextString());
									} else if ("seriesnumber".equals(key2)) {
										menu.childCount = reader.nextInt();
									} else {
										reader.skipValue();
									}
								}
								menus.add(menu);
								reader.endObject();
							}
							reader.endArray();

						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return menus;
	}

	public static Detail parseDetail(String data) throws JSONException {
		JSONObject detailJson = new JSONObject(data);
		JSONObject infoJson = detailJson.getJSONObject("entity");
		Detail detail = new Detail();
		detail.contentID = infoJson.getString("id");
		detail.description = infoJson.getString("description");
		detail.director = infoJson.getString("directorDisplay");
		detail.name = infoJson.getString("name");
		detail.type = infoJson.getInt("type");
		detail.lastUpdateEpisode = infoJson.getInt("lastUpdateEpisode");
		if (infoJson.has("tag")) {
			detail.category = infoJson.getString("tag");
		} else {
			detail.category = "";
		}
		detail.zone = infoJson.getString("zone");
		detail.imgPaths.add(infoJson.getString("poster"));
		String actorDisplay = infoJson.getString("actorDisplay");
		APPLog.printInfo("actorDisplay:" + actorDisplay);
		String[] actors = actorDisplay.split(",");
		int len = actors.length;
		for (int i = 0; i < len; i++) {
			detail.actors.add(actors[i]);
		}

		JSONArray sourceArray = infoJson.getJSONArray("episodes");
		len = sourceArray.length();
		for (int i = 0; i < len; i++) {
			JSONObject sourceJson = sourceArray.getJSONObject(i);
			String name = sourceJson.getString("name");
			String id = sourceJson.getString("id");
			String previewImg = sourceJson.getString("thumbnail");
			JSONArray sourceUrlArray = sourceJson.getJSONArray("sources");
			if (sourceUrlArray.length() > 0) {
				JSONObject urlJson = sourceUrlArray.getJSONObject(0);
				String url = urlJson.getString("playUrl");

				Source source = new Source();
				source.name = name;
				source.url = url;
				source.id = id;
				source.previewImg = previewImg;
				detail.sources.add(source);
			}
		}
		return detail;
	}

	public static List<Content> convertDataToContent(String data) {
		List<Content> contents = new ArrayList<Content>();
		JsonReader reader = new JsonReader(new StringReader(data));
		try {
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String key2 = reader.nextName();
				if ("entity".equals(key2)) {
					reader.beginObject();
					String parentID = null;
					while (reader.hasNext()) {
						String key = reader.nextName();
						if ("listInfo".equals(key)) {
							reader.beginArray();
							while (reader.hasNext()) {
								reader.beginObject();
								Item content = new Item();
								String lightWord = null;
								while (reader.hasNext()) {
									String key1 = reader.nextName();
									if ("id".equals(key1)) {
										content.contentID = reader.nextString();
									} else if ("name".equals(key1)) {
										content.name = reader.nextString();
									} else if ("light".equals(key1)) {
										lightWord = reader.nextString();
									} else if ("verticalPicture".equals(key1)) {
										content.imgPaths = new ArrayList<String>();
										content.imgPaths.add(reader.nextString());
									} else if ("lastUpdateEpisode".equals(key1)) {
										content.lastUpdateEpisode = reader.nextString();
									} else {
										reader.skipValue();
									}
								}
								String name = translateName(content.name, lightWord);
								content.name = name;
								content.parentID = parentID;
								contents.add(content);
								reader.endObject();
							}
							reader.endArray();
						} else if ("menuId".equals(key)) {
							parentID = reader.nextString();
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return contents;
	}

	private static String translateName(String name, String light) {
		if (TextUtils.isEmpty(light)) {
			return name;
		}
		int len = light.length();
		for (int i = 0; i < len; i++) {
			String word = String.valueOf(light.charAt(i));
			name = name.replaceAll(word, "<font color='#ffd133'>" + word + "</font>");
		}
		return name;
	}

	public static List<Content> convertDataToNewContent(String data) {
		List<Content> contents = new ArrayList<Content>();
		JsonReader reader = new JsonReader(new StringReader(data));
		try {
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String key = reader.nextName();
				if ("entity".equals(key)) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						Item content = new Item();
						while (reader.hasNext()) {
							String key1 = reader.nextName();
							if ("id".equals(key1)) {
								content.contentID = reader.nextString();
							} else if ("name".equals(key1)) {
								content.name = reader.nextString();
							} /*
							 * else if ("horizontalPicture".equals(key1)) {
							 * content
							 * .setHorizontalPicture(reader.nextString()); }
							 */else if ("verticalPicture".equals(key1)) {
								content.imgPaths = new ArrayList<String>();
								content.imgPaths.add(reader.nextString());
							}/*
							 * else if ("starlevel".equals(key1)) {
							 * content.setStarLevel(reader.nextInt()); } else if
							 * ("actors".equals(key1)) {
							 * content.setActors(reader.nextString()); } else if
							 * ("viewpoint".equals(key1)) {
							 * content.setViewpoint(reader.nextString()); } else
							 * if ("isNew".equals(key1)) {
							 * content.setIsNew(reader.nextInt()); }
							 */else if ("lastUpdateEpisode".equals(key1)) {
								content.lastUpdateEpisode = reader.nextString();
							} else {
								reader.skipValue();
							}
						}
						contents.add(content);
						reader.endObject();
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contents;
	}

	public static List<TagGroup> convertDataToTagGroup(String data) {
		List<TagGroup> tagGroups = new ArrayList<TagGroup>();
		JsonReader reader = new JsonReader(new StringReader(data));
		try {
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String key = reader.nextName();
				if ("entity".equals(key)) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						TagGroup tagGroup = new TagGroup();
						while (reader.hasNext()) {
							String key1 = reader.nextName();
							if ("id".equals(key1)) {
								tagGroup.setId(reader.nextString());
							} else if ("groupName".equals(key1)) {
								tagGroup.setGroupName(reader.nextString());
							} else if ("tags".equals(key1)) {
								reader.beginArray();
								List<Tag> tags = new ArrayList<Tag>();
								while (reader.hasNext()) {
									reader.beginObject();
									Tag tag = new Tag();
									while (reader.hasNext()) {
										String key2 = reader.nextName();
										if ("code".equals(key2)) {
											tag.setCode(reader.nextString());
										} else if ("tagName".equals(key2)) {
											tag.setTagName(reader.nextString());
										} else {
											reader.skipValue();
										}
									}
									tags.add(tag);
									reader.endObject();
								}
								tagGroup.setTags(tags);
								reader.endArray();
							} else {
								reader.skipValue();
							}
						}
						tagGroups.add(tagGroup);
						reader.endObject();
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return tagGroups;
	}

	public static List<Content> convertModelsToContents(List<ModelSeat> modelSeats) {
		List<Content> contents = new ArrayList<Content>();
		for (ModelSeat modelSeat : modelSeats) {
			Item content = new Item();
			content.contentID = modelSeat.getActionValue();
			content.name = modelSeat.getName();
			ArrayList<String> imagepaths = new ArrayList<String>();
			imagepaths.add(modelSeat.getPoster());
			content.imgPaths = imagepaths;
			contents.add(content);
		}
		return contents;
	}

	// public static List<com.pkit.launcher.service.aidl.Content>
	// convertToContents(List<Content> contents1) {
	// List<com.pkit.launcher.service.aidl.Content> contents = new
	// ArrayList<com.pkit.launcher.service.aidl.Content>();
	// for (Content content : contents1) {
	// com.pkit.launcher.service.aidl.Item item = new
	// com.pkit.launcher.service.aidl.Item();
	// item.parentID = content.getId();
	// item.contentID = content.getId();
	// item.name = content.getName();
	// ArrayList<String> images = new ArrayList<String>();
	// images.add(content.getVerticalPicture());
	// images.add(content.getHorizontalPicture());
	// item.imgPaths = images;
	// contents.add(item);
	// }
	// return contents;
	// }

	public static DeviceConfig convertDataToDeviceConfig(String data) {
		DeviceConfig config = new DeviceConfig();
		JsonReader reader = new JsonReader(new StringReader(data));
		try {
			reader.setLenient(true);
			reader.beginObject();
			while (reader.hasNext()) {
				String key = reader.nextName();
				if ("entity".equals(key)) {
					reader.beginObject();
					while (reader.hasNext()) {
						String key1 = reader.nextName();
						if ("deviceId".equals(key1)) {
							config.setDeviceId(reader.nextString());
						} else if ("accountId".equals(key1)) {
							config.setAccountId(reader.nextString());
						} else if ("userId".equals(key1)) {
							config.setUserId(reader.nextString());
						} else if ("password".equals(key1)) {
							config.setPassword(reader.nextString());
						} else if ("regionId".equals(key1)) {
							config.setRegionId(reader.nextString());
						} else if ("templateId".equals(key1)) {
							config.setTemplateId(reader.nextString());
						} else if ("state".equals(key1)) {
							config.setState(reader.nextString());
						} else if ("token".equals(key1)) {
							config.setToken(reader.nextString());
						} else if ("ruleId".equals(key1)) {
							config.setRuleId(reader.nextString());
						} else if ("timestamp".equals(key1)) {
							config.setTimestamp(reader.nextLong());
						} else if ("maketype".equals(key1)) {
							config.setMakeType(reader.nextInt());
						} else if ("addressList".equals(key1)) {
							reader.beginArray();
							List<Address> addrs = new ArrayList<Address>();
							while (reader.hasNext()) {
								reader.beginObject();
								Address addr = new Address();
								while (reader.hasNext()) {
									String key2 = reader.nextName();
									if ("url".equals(key2)) {
										addr.setUrl(reader.nextString());
									} else if ("code".equals(key2)) {
										addr.setCode(reader.nextString());
									} else {
										reader.skipValue();
									}
								}
								reader.endObject();
								addrs.add(addr);
							}
							config.setAddressList(addrs);
							reader.endArray();
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return config;
	}

	public static Upgrade parseUpgrade(String data) {
		Upgrade upgrade = new Upgrade();
		JSONObject detailJson = null;
		try {
			detailJson = new JSONObject(data);
			JSONObject infoJson = detailJson.getJSONObject("entity");
			upgrade.setMd5(infoJson.getString("md5"));
			upgrade.setPackageLocation(infoJson.getString("packageLocation"));
			upgrade.setUpgradeState(infoJson.getString("upgradeState"));
			upgrade.setVersionSeq(infoJson.getString("versionSeq"));
			upgrade.setVersionName(infoJson.getString("versionName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return upgrade;
	}
}
