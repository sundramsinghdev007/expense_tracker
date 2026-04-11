package com.sundram.expense_tracker.ocr;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class OcrViewModel_Factory implements Factory<OcrViewModel> {
  private final Provider<Context> contextProvider;

  public OcrViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OcrViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static OcrViewModel_Factory create(Provider<Context> contextProvider) {
    return new OcrViewModel_Factory(contextProvider);
  }

  public static OcrViewModel newInstance(Context context) {
    return new OcrViewModel(context);
  }
}
