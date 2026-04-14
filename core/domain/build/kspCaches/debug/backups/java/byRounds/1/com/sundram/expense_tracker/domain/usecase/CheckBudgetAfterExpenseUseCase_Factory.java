package com.sundram.expense_tracker.domain.usecase;

import com.sundram.expense_tracker.domain.repository.BudgetRepository;
import com.sundram.expense_tracker.domain.repository.ExpenseRepository;
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
public final class CheckBudgetAfterExpenseUseCase_Factory implements Factory<CheckBudgetAfterExpenseUseCase> {
  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public CheckBudgetAfterExpenseUseCase_Factory(Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public CheckBudgetAfterExpenseUseCase get() {
    return newInstance(budgetRepositoryProvider.get(), expenseRepositoryProvider.get());
  }

  public static CheckBudgetAfterExpenseUseCase_Factory create(
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new CheckBudgetAfterExpenseUseCase_Factory(budgetRepositoryProvider, expenseRepositoryProvider);
  }

  public static CheckBudgetAfterExpenseUseCase newInstance(BudgetRepository budgetRepository,
      ExpenseRepository expenseRepository) {
    return new CheckBudgetAfterExpenseUseCase(budgetRepository, expenseRepository);
  }
}
