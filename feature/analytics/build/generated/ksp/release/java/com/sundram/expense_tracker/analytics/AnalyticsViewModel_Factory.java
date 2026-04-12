package com.sundram.expense_tracker.analytics;

import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class AnalyticsViewModel_Factory implements Factory<AnalyticsViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  public AnalyticsViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
  }

  @Override
  public AnalyticsViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get());
  }

  public static AnalyticsViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider) {
    return new AnalyticsViewModel_Factory(getExpensesUseCaseProvider);
  }

  public static AnalyticsViewModel newInstance(GetExpensesUseCase getExpensesUseCase) {
    return new AnalyticsViewModel(getExpensesUseCase);
  }
}
