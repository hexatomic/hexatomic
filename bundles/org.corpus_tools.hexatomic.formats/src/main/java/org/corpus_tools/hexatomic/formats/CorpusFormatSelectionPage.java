package org.corpus_tools.hexatomic.formats;



import java.util.Optional;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class CorpusFormatSelectionPage<F> extends WizardPage {

  protected Button btnExb;
  protected Button btnPaulaXml;

  public CorpusFormatSelectionPage(String pageName) {
    super(pageName);
  }

  public CorpusFormatSelectionPage(String pageName, String title, ImageDescriptor titleImage) {
    super(pageName, title, titleImage);
  }

  @Override
  public void createControl(Composite parent) {
  
    setPageComplete(false);
  
    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(1, false));
  
    SelectionAdapter checkboxSelectionAdapter = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPageComplete(getSelectedFormat().isPresent());
      }
    };
  
    btnExb = new Button(container, SWT.RADIO);
    btnExb.addSelectionListener(checkboxSelectionAdapter);
    btnExb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnExb.setBounds(0, 0, 112, 17);
    btnExb.setText("EXMARaLDA format (*.exb)");
  
    btnPaulaXml = new Button(container, SWT.RADIO);
    btnPaulaXml.addSelectionListener(checkboxSelectionAdapter);
    btnPaulaXml.setText("PAULA format");
  }


  /**
   * Return the currently selected format.
   * 
   * @return The format as enum variant.
   */
  public abstract Optional<F> getSelectedFormat();

}
