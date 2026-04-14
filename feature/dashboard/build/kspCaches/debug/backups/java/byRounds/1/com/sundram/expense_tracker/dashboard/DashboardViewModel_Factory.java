package com.sundram.expense_tracker.dashboard;

import android.content.Context;
import com.sundram.expense_tracker.domain.usecase.GetBudgetsUseCase;
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<GetBudgetsUseCase> getBudgetsUseCaseProvider;

  public DashboardViewModel_Factory(Provider<Context> contextProvider,
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<GetBudgetsUseCase> getBudgetsUseCaseProvider) {
    this.contextProvider = contextProvider;
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.getBudgetsUseCaseProvider = getBudgetsUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(contextProvider.get(), getExpensesUseCaseProvider.get(), getBudgetsUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<Context> contextProvider,
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<GetBudgetsUseCase> getBudgetsUseCaseProvider) {
    return new DashboardViewModel_Factory(contextProvider, getExpensesUseCaseProvider, getBudgetsUseCaseProvider);
  }

  public static DashboardViewModel newInstance(Context context,
      GetExpensesUseCase getExpensesUseCase, GetBudgetsUseCase getBudgetsUseCase) {
    return new DashboardViewModel(context, getExpensesUseCase, getBudgetsUseCase);
  }
}
