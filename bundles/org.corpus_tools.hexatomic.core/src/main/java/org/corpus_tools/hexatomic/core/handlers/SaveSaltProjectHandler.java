package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.di.annotations.Execute;

public class SaveSaltProjectHandler {

  @Inject
  ErrorService errorService;

  @Execute
  public void execute() {
    errorService.showError("Not implemted yet", "The save functionality is not implemented yet",
        SaveSaltProjectHandler.class);
  }
}
