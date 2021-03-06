package com.chinanetcenter.api.wsbox;


import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理
 *
 * @version 1.0
 * @since 2014/02/14
 * Modified by chenld1 on 2015/8/14.
 */
public class OperationManager {
    /**
     * 删除文件
     */
    public HttpClientResult delete(String bucketName, String fileKey) throws WsClientException {
        String entry = bucketName + ":" + fileKey;
        String encodeEntryURI = EncodeUtils.urlsafeEncode(entry);
        String url = Config.MGR_URL + "/delete/" + encodeEntryURI;
        Map<String, String> headMap = new HashMap<String, String>();
        String deleteToken = TokenUtil.getDeleteToken(bucketName, fileKey);
        headMap.put("Authorization", deleteToken);
        return HttpClientUtil.httpPost(url, null, headMap);
    }

    public HttpClientResult stat(String bucketName, String fileKey) throws WsClientException {
        String entry = bucketName + ":" + fileKey;
        String encodedEntryURI = EncodeUtils.urlsafeEncode(entry);
        String url = Config.MGR_URL + "/stat/" + encodedEntryURI;
        Map<String, String> headMap = new HashMap<String, String>();
        String statToken = TokenUtil.getStatToken(bucketName, fileKey);
        headMap.put("Authorization", statToken);
        return HttpClientUtil.httpGet(url, headMap);
    }

    public HttpClientResult fileList(String bucketName, String limit, String prefix, String mode, String marker) throws WsClientException {
        String url = "/list?" + "bucket=" + bucketName;
        if (StringUtil.isNotEmpty(marker)) {
            url += "&marker=" + marker;
        }
        if (StringUtil.isNotEmpty(limit)) {
            url += "&limit=" + limit;
        }
        if (StringUtil.isNotEmpty(prefix)) {
            url += "&prefix=" + EncodeUtils.urlsafeEncode(prefix);
        }
        if (StringUtil.isNotEmpty(mode)) {
            url += "&mode=" + mode;
        }
        Map<String, String> headMap = new HashMap<String, String>();
        String listToken = TokenUtil.getFileListToken(url);
        url = Config.MGR_URL + url;
        headMap.put("Authorization", listToken);
        return HttpClientUtil.httpGet(url, headMap);
    }

    /**
     * 更新镜像资源
     */
    public HttpClientResult prefetch(String bucketName, String... fileKeys) throws WsClientException {
        if (StringUtil.isEmpty(bucketName)) {
            throw new WsClientException(-1,"buckect name cannot be empty");
        }
        int length = fileKeys.length;
        for (int i = 0; i < length; i++) {
            if (StringUtil.isEmpty(fileKeys[i])) {
                throw new WsClientException(-1,"filekey" + i + " cannot be empty");
            }
        }
        StringBuffer sb = new StringBuffer(bucketName + ":" + EncodeUtils.urlsafeEncode(fileKeys[0]));
        for (int i = 1; i < length; i++) {
            sb.append("|").append(EncodeUtils.urlsafeEncode(fileKeys[i]));
        }
        String param = sb.toString();
        String encodeEntryUrl = EncodeUtils.urlsafeEncode(param);
        String url = Config.MGR_URL + "/prefetch/" + encodeEntryUrl;
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/prefetch/" + encodeEntryUrl + "\n").getBytes(), Config.SK));
        String Authorization = Config.AK + ":" + value;

        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", Authorization);

        return HttpClientUtil.httpPost(url, null, headMap);
    }

    /**
     * @param bucketName 空间名
     * @param fileKeys   文件名列表
     */
    public HttpClientResult prefetch(String bucketName, ArrayList<String> fileKeys) throws WsClientException {
        int length = fileKeys.size();
        String[] fileKeyArray = new String[length];
        int i = 0;
        for (String s : fileKeys) {
            fileKeyArray[i++] = s;
        }
        return prefetch(bucketName, fileKeyArray);
    }

    public HttpClientResult copy(String bucketNameSrc, String fileKeySrc, String bucketNameDest, String fileKeyDest) throws WsClientException {
        if (StringUtil.isEmpty(bucketNameSrc)) {
            throw new WsClientException(-1,"bucketNameSrc cannot be empty");
        }
        if (StringUtil.isEmpty(fileKeySrc)) {
            throw new WsClientException(-1,"fileKeySrc cannot be empty");
        }
        if (StringUtil.isEmpty(fileKeyDest)) {
            throw new WsClientException(-1,"fileKeyDest cannot be empty");
        }
        if (StringUtil.isEmpty(bucketNameDest)) {
            throw new WsClientException(-1,"bucketNameDest cannot be empty");
        }
        String entrySrc = bucketNameSrc + ":" + fileKeySrc;
        String entryDest = bucketNameDest + ":" + fileKeyDest;
        String encodeEntryURISrc = EncodeUtils.urlsafeEncode(entrySrc);
        String encodeEntryURIDest = EncodeUtils.urlsafeEncode(entryDest);
        String url = Config.MGR_URL + "/copy/" + encodeEntryURISrc + "/" + encodeEntryURIDest;
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/copy/" + encodeEntryURISrc + "/" + encodeEntryURIDest + "\n").getBytes(), Config.SK));
        String Authorization = Config.AK + ":" + value;

        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", Authorization);

        return HttpClientUtil.httpPost(url, null, headMap);

    }

    public HttpClientResult move(String bucketNameSrc, String fileKeySrc, String bucketNameDest, String fileKeyDest) throws WsClientException {
        if (StringUtil.isEmpty(bucketNameSrc)) {
            throw new WsClientException(-1,"bucketNameSrc cannot be empty");
        }
        if (StringUtil.isEmpty(fileKeySrc)) {
            throw new WsClientException(-1,"fileKeySrc cannot be empty");
        }
        if (StringUtil.isEmpty(fileKeyDest)) {
            throw new WsClientException(-1,"fileKeyDest cannot be empty");
        }
        if (StringUtil.isEmpty(bucketNameDest)) {
            throw new WsClientException(-1,"bucketNameDest cannot be empty");
        }
        String entrySrc = bucketNameSrc + ":" + fileKeySrc;
        String entryDest = bucketNameDest + ":" + fileKeyDest;
        String encodeEntryURISrc = EncodeUtils.urlsafeEncode(entrySrc);
        String encodeEntryURIDest = EncodeUtils.urlsafeEncode(entryDest);
        String url = Config.MGR_URL + "/move/" + encodeEntryURISrc + "/" + encodeEntryURIDest;
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/move/" + encodeEntryURISrc + "/" + encodeEntryURIDest + "\n").getBytes(), Config.SK));
        String Authorization = Config.AK + ":" + value;

        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", Authorization);

        return HttpClientUtil.httpPost(url, null, headMap);
    }

    public HttpClientResult fops(String bucketName, String fileKey, String fops, String notifyURL, String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fops";
        StringBuffer bodySB = new StringBuffer("");
        bodySB.append("bucket=").append(EncodeUtils.urlsafeEncode(bucketName));
        bodySB.append("&key=").append(EncodeUtils.urlsafeEncode(fileKey));
        bodySB.append("&fops=").append(EncodeUtils.urlsafeEncode(fops));
        if (StringUtils.isNotEmpty(notifyURL)){
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyURL));
        }
        if (StringUtils.isNotEmpty(force)){
            bodySB.append("&force=").append(force);
        }
        if (StringUtils.isNotEmpty(separate)){
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fops" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url,headMap,bodySB.toString());
    }
}
