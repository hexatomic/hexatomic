package org.corpus_tools.hexatomic.formats;

import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

	public ImportWizard() {
		super();
        setNeedsProgressMonitor(true);
	}

    @Override
	public String getWindowTitle() {
      return "Import a corpus project from a different file format";
	}

    @Override
    public void addPages() {
      addPage(new CorpusPathSelectionPage(Type.Import));
    }

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
