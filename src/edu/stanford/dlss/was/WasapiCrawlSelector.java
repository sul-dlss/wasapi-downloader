package edu.stanford.dlss.was;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WasapiCrawlSelector {

  public Map<Integer, List<WasapiFile>> crawlIdToFiles;

  public WasapiCrawlSelector(WasapiFile[] candidateFiles) {
    setCrawlIdToFiles(candidateFiles);
  }

  // expects lastKnownCrawlId to be validated before it gets here: expects positive int)
  public List<Integer> getSelectedCrawlIds(int lastKnownCrawlId) {
    List<Integer> selectedCrawlIds = new ArrayList<Integer>();
    for (Integer crawlId : crawlIdToFiles.keySet()) {
      if (crawlId > lastKnownCrawlId) {
        selectedCrawlIds.add(crawlId);
      }
    }
    return selectedCrawlIds;
  }

  // expects crawlsStartAfterStr to be validated before it gets here: expects ISO8601 UTC, e.g. "2017-04-26T17:53:16Z"
  public List<Integer> getSelectedCrawlIds(String crawlsStartAfterStr) {
    List<Integer> selectedCrawlIds = new ArrayList<Integer>();
    for (Integer crawlId : crawlIdToFiles.keySet()) {
      WasapiFile file = crawlIdToFiles.get(crawlId).get(0);
      String crawlStartDateStr = file.getCrawlStartDateStr();
      if (crawlStartDateStr.compareTo(crawlsStartAfterStr) > 0)
        selectedCrawlIds.add(crawlId);
    }
    return selectedCrawlIds;
  }


  private void setCrawlIdToFiles(WasapiFile[] candidateFiles) {
    if (crawlIdToFiles == null) {
      crawlIdToFiles = new HashMap<Integer, List<WasapiFile>>();
      for (WasapiFile file : candidateFiles) {
        Integer crawlIdInteger = Integer.valueOf(file.getCrawlId());
        List<WasapiFile> files;
        if (crawlIdToFiles.isEmpty() || crawlIdToFiles.get(crawlIdInteger) == null)
          files = new ArrayList<WasapiFile>();
        else
          files = crawlIdToFiles.get(crawlIdInteger);
        files.add(file);
        crawlIdToFiles.put(crawlIdInteger, files);
      }
    }
  }
}
