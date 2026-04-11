package com.sundram.expense_tracker.budgets;

import com.sundram.expense_tracker.domain.usecase.GetBudgetsUseCase;
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase;
import com.sundram.expense_tracker.domain.usecase.UpsertBudgetUseCase;
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
public final class BudgetsViewModel_Factory implements Factory<BudgetsViewModel> {
  private final Provider<GetBudgetsUseCase> getBudgetsUseCaseProvider;

  private final Provider<UpsertBudgetUseCase> upsertBudgetUseCaseProvider;

  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  public BudgetsViewModel_Factory(Provider<GetBudgetsUseCase> getBudgetsUseCaseProvider,
      Provider<UpsertBudgetUseCase> upsertBudgetUseCaseProvider,
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider) {
    this.getBudgetsUseCaseProvider = getBudgetsUseCaseProvider;
    this.upsertBudgetUseCaseProvider = upsertBudgetUseCaseProvider;
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
  }

  @Override
  public BudgetsViewModel get() {
    return newInstance(getBudgetsUseCaseProvider.get(), upsertBudgetUseCaseProvider.get(), getExpensesUseCaseProvider.get());
  }

  public static BudgetsViewModel_Factory create(
      Provider<GetBudgetsUseCase> getBudgetsUseCaseProvider,
      Provider<UpsertBudgetUseCase> upsertBudgetUseCaseProvider,
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider) {
    return new BudgetsViewModel_Factory(getBudgetsUseCaseProvider, upsertBudgetUseCaseProvider, getExpensesUseCaseProvider);
  }

  public static BudgetsViewModel newInstance(GetBudgetsUseCase getBudgetsUseCase,
      UpsertBudgetUseCase upsertBudgetUseCase, GetExpensesUseCase getExpensesUseCase) {
    return new BudgetsViewModel(getBudgetsUseCase, upsertBudgetUseCase, getExpensesUseCase);
  }
}
