/*-
 * #%L
 * org.corpus_tools.hexatomic.corpusstructureeditor
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
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
package org.corpus_tools.hexatomic.corpusedit;

import java.net.URL;

import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.core.SNamedElement;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class CorpusLabelProvider extends LabelProvider {

	private ImageDescriptor documentImage;

	@Override
	public Image getImage(Object element) {
		if (element instanceof SDocument) {
			return ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
					"icons/fontawesome/file-alt-regular.png");
		} else if (element instanceof SCorpusGraph) {
			return ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
					"icons/fontawesome/project-diagram-solid.png");
		} else if (element instanceof SCorpus) {
			return ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
					"icons/fontawesome/folder-regular.png");
		} else {
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		String result = null;

		if (element instanceof SNamedElement) {
			SNamedElement n = (SNamedElement) element;
			String name = n.getName();
			if (name == null || name.isEmpty()) {
				// Try the ID
				if (element instanceof IdentifiableElement) {
					result = ((IdentifiableElement) element).getId();
				} else {
					result = "<unnamed " + element.getClass().getSimpleName() + ">";
				}
			} else {
				result = n.getName();
			}
		}

		if (result == null || result.isEmpty()) {
			result = "<unknown>";
		}

		return result;
	}

	protected ImageDescriptor getDocumentImage() {
		if (documentImage == null) {
			Bundle bundle = FrameworkUtil.getBundle(CorpusLabelProvider.class);
			URL url = FileLocator.find(bundle, new Path("icons/fontawesome/file-alt-regular.png"), null);
			if (url != null) {
				documentImage = ImageDescriptor.createFromURL(url);
			}
		}
		return documentImage;
	}
}
