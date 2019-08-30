package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenSaltProjectHandler {
	
	@Inject
	private ProjectManager projectManager;
	
	private String lastPath;
	
	@Execute
	public void execute(Shell shell) {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		if(lastPath != null) {
			dialog.setFilterPath(lastPath);
		}
		String resultPath = dialog.open();
		if(resultPath != null) {
			projectManager.open(URI.createFileURI(resultPath));
			lastPath = resultPath;
		}
	}
}
