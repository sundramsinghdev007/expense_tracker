package com.sundram.expense_tracker.settings;

import android.content.Context;
import com.sundram.expense_tracker.domain.usecase.ExportExpensesUseCase;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ExportExpensesUseCase> exportExpensesUseCaseProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<ExportExpensesUseCase> exportExpensesUseCaseProvider) {
    this.contextProvider = contextProvider;
    this.exportExpensesUseCaseProvider = exportExpensesUseCaseProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), exportExpensesUseCaseProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ExportExpensesUseCase> exportExpensesUseCaseProvider) {
    return new SettingsViewModel_Factory(contextProvider, exportExpensesUseCaseProvider);
  }

  public static SettingsViewModel newInstance(Context context,
      ExportExpensesUseCase exportExpensesUseCase) {
    return new SettingsViewModel(context, exportExpensesUseCase);
  }
}
