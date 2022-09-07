package org.corpus_tools.hexatomic.mediaplayer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SMedialDS;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class MediaPlayer {

  @Inject
  private ESelectionService selectionService;

  @Inject
  private ProjectManager projectManager;

  @Inject
  private ErrorService errorService;

  private Combo cbMediaDataSources;
  private Browser mediaBrowser;

  @PostConstruct
  public void postConstruct(Composite parent, MPart part) {
    parent.setLayout(new GridLayout(1, false));

    String documentID = part.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
    Optional<SDocument> doc = projectManager.getDocument(documentID);

    cbMediaDataSources = new Combo(parent, SWT.NONE);
    cbMediaDataSources.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    mediaBrowser = new Browser(parent, SWT.NONE);
    mediaBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    if (doc.isPresent()) {
      SDocumentGraph docGraph = doc.get().getDocumentGraph();
      List<SMedialDS> medialDSs = docGraph.getMedialDSs();
      if (medialDSs != null) {
        String[] items = medialDSs.stream().map(ds -> ds.getMediaReference().toString())
            .collect(Collectors.toList()).toArray(new String[0]);

        cbMediaDataSources.setItems(items);
        if (items.length > 0) {
          cbMediaDataSources.select(0);
          openSelectedMediaFile();
        }
      }
    }
  }

  private void openSelectedMediaFile() {
    if (cbMediaDataSources.getItemCount() > 0 && cbMediaDataSources.getSelectionIndex() >= 0) {

      Bundle thisBundle = FrameworkUtil.getBundle(MediaPlayer.class);

      String fileUri = cbMediaDataSources.getItem(cbMediaDataSources.getSelectionIndex());
      Path filePath = Path.of(fileUri);
      try {
        String mimeType = Files.probeContentType(filePath);
        File playerTemplate = null;
        if (mimeType.startsWith("video/")) {
          playerTemplate = new File(
              FileLocator.resolve(thisBundle.getResource("html/video_player.html")).toURI());
        } else if (mimeType.startsWith("audio/")) {
          playerTemplate = new File(
              FileLocator.resolve(thisBundle.getResource("html/audio_player.html")).toURI());
        }

        if (playerTemplate != null) {
          String templateAsHtml = Files.readString(playerTemplate.toPath());
          templateAsHtml = templateAsHtml.replaceAll("\\$\\$mediafile\\$\\$", fileUri);
          mediaBrowser.setText(templateAsHtml);
        }

      } catch (IOException | URISyntaxException ex) {
        errorService.handleException("Could not open file in media player", ex, MediaPlayer.class);
      }
    }
  }

  @Inject
  void setSelection(
      @org.eclipse.e4.core.di.annotations.Optional @Named(IServiceConstants.ACTIVE_SELECTION) SNode node) {
    // TODO: allow to automatically play the current selection
  }
}
