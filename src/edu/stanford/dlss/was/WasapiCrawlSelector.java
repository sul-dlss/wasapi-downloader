package edu.stanford.dlss.was;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WasapiCrawlSelector {

  protected Map<Integer, List<WasapiFile>> crawlIdToFiles = new HashMap<Integer, List<WasapiFile>>();

  public WasapiCrawlSelector(WasapiFile[] candidateFiles) {
    addCandidateFiles(candidateFiles);
  }

  public WasapiCrawlSelector(List<WasapiResponse> respList) {
    for (WasapiResponse resp : respList)
      addCandidateFiles(resp.getFiles());
  }

  /**
   * expects lastKnownCrawlId to be validated before it gets here: expects positive int
   * if arg is 0, it will return all WARCs in the candidate files
   */
  public List<Integer> getSelectedCrawlIds(int lastKnownCrawlId) {
    List<Integer> selectedCrawlIds = new ArrayList<Integer>();
    for (Integer crawlId : crawlIdToFiles.keySet()) {
      if (crawlId > lastKnownCrawlId) {
        selectedCrawlIds.add(crawlId);
      }
    }
    return selectedCrawlIds;
  }

  public List<WasapiFile> getFilesForCrawl(Integer crawlId) {
    return crawlIdToFiles.get(crawlId);
  }

  private void addCandidateFiles(WasapiFile[] candidateFiles) {
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
