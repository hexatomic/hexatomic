/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.core.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public class AboutDialog extends Dialog {

  private final Font headerFont;
  private final Font versionFont;


  /**
   * Create the dialog.
   * 
   * @param parentShell The parent
   */
  public AboutDialog(Shell parentShell) {
    super(parentShell);

    Font dialogFont = JFaceResources.getDialogFont();
    FontDescriptor headerFontDescriptor = FontDescriptor.createFrom(dialogFont).setHeight(32);
    headerFont = headerFontDescriptor.createFont(parentShell.getDisplay());

    FontDescriptor boldFontDescriptor =
        FontDescriptor.createFrom(dialogFont).setStyle(SWT.BOLD).setHeight(16);
    versionFont = boldFontDescriptor.createFont(parentShell.getDisplay());

  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("About Hexatomic");
  }

  private static String getFullVersion() {
    Version v = FrameworkUtil.getBundle(AboutDialog.class).getVersion();
    return String.format("%d.%d.%d", v.getMajor(), v.getMinor(), v.getMicro());
  }

  private static String getShortVersion() {
    Version v = FrameworkUtil.getBundle(AboutDialog.class).getVersion();
    return String.format("%d.%d", v.getMajor(), v.getMinor());
  }

  /**
   * Create the user documentation URL for the current version.
   * 
   * @return The complete URL.
   */
  public static String getOnlineDocumentationUrl() {
    return "https://hexatomic.github.io/hexatomic/user/v" + getShortVersion() + "/";
  }

  private static String getRepoLink(String path) {
    return "https://github.com/hexatomic/hexatomic/blob/v" + getFullVersion() + "/" + path;
  }

  /**
   * Create contents of the dialog.
   * 
   * @param parent The parent
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);

    Label lblHeader = new Label(container, SWT.NONE);
    lblHeader.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
    lblHeader.setText("Hexatomic");
    lblHeader.setFont(headerFont);

    Label lblVersion = new Label(container, SWT.NONE);
    lblVersion.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lblVersion.setText("Version: " + getFullVersion());
    lblVersion.setFont(versionFont);

    Link lnkHomepage = new Link(container, SWT.NONE);
    lnkHomepage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkHomepage.setText("Homepage: <a>https://hexatomic.github.io/</a>");
    lnkHomepage.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch("https://hexatomic.github.io/");
      }
    });


    Link lnkDocumentation = new Link(container, SWT.NONE);
    lnkDocumentation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkDocumentation.setText("<a>Online User Documentation</a>");
    lnkDocumentation.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch(getOnlineDocumentationUrl());
      }
    });

    Link lnkAuthors = new Link(container, SWT.NONE);
    lnkAuthors.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkAuthors.setText("© 2018ff. <a>Hexatomic project team</a>");


    lnkAuthors.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch("https://github.com/orgs/hexatomic/teams/project/members");
      }
    });


    Link lnkIssue = new Link(container, SWT.NONE);
    lnkIssue.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkIssue.setText("<a>Create a bug report</a> to notify us of an error in Hexatomic.");

    lnkIssue.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch(
            "https://github.com/hexatomic/hexatomic/issues/new?assignees=&labels=bug&template=bug_report.md&title=Bug in version "
                + getFullVersion());
      }
    });

    Label separator = new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
    separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    Link lnkLicense = new Link(container, SWT.CENTER);
    lnkLicense.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkLicense
        .setText("Published as Free and Open Source Software under the <a>Apache License 2.0</a>.");

    lnkLicense.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch("https://spdx.org/licenses/Apache-2.0.html");
      }
    });

    Link lnkCitation = new Link(container, SWT.CENTER);
    lnkCitation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkCitation.setText(
        "See the <a>CITATION.cff</a> file for a list of included software and citation notes.");

    lnkCitation.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch(getRepoLink("CITATION.cff"));
      }
    });

    Link lnkThirdParty = new Link(container, SWT.CENTER);
    lnkThirdParty.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lnkThirdParty.setText(
        "License files of included software are located in the <a>THIRD-PARTY</a> folder.");

    lnkThirdParty.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch(getRepoLink("THIRD-PARTY"));
      }
    });



    return container;
  }

  /**
   * Create contents of the button bar.
   * 
   * @param parent The parent
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  /**
   * Return the initial size of the dialog.
   */
  @Override
  protected Point getInitialSize() {
    return new Point(557, 380);
  }

  @Override
  public boolean close() {
    headerFont.dispose();
    versionFont.dispose();
    return super.close();
  }

}
