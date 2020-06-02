package org.corpus_tools.hexatomic.core;

import org.eclipse.e4.ui.di.UISynchronize;

public final class DummySync extends UISynchronize {
  @Override
  public void syncExec(Runnable runnable) {
    runnable.run();
  }

  @Override
  public void asyncExec(Runnable runnable) {
    runnable.run();
  }
}