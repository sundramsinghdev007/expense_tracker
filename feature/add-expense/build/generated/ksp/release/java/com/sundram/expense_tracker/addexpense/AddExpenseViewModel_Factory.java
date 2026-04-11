package com.sundram.expense_tracker.addexpense;

import com.sundram.expense_tracker.domain.usecase.AddExpenseUseCase;
import com.sundram.expense_tracker.domain.usecase.CheckBudgetAfterExpenseUseCase;
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
public final class AddExpenseViewModel_Factory implements Factory<AddExpenseViewModel> {
  private final Provider<AddExpenseUseCase> addExpenseUseCaseProvider;

  private final Provider<CheckBudgetAfterExpenseUseCase> checkBudgetAfterExpenseUseCaseProvider;

  public AddExpenseViewModel_Factory(Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<CheckBudgetAfterExpenseUseCase> checkBudgetAfterExpenseUseCaseProvider) {
    this.addExpenseUseCaseProvider = addExpenseUseCaseProvider;
    this.checkBudgetAfterExpenseUseCaseProvider = checkBudgetAfterExpenseUseCaseProvider;
  }

  @Override
  public AddExpenseViewModel get() {
    return newInstance(addExpenseUseCaseProvider.get(), checkBudgetAfterExpenseUseCaseProvider.get());
  }

  public static AddExpenseViewModel_Factory create(
      Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<CheckBudgetAfterExpenseUseCase> checkBudgetAfterExpenseUseCaseProvider) {
    return new AddExpenseViewModel_Factory(addExpenseUseCaseProvider, checkBudgetAfterExpenseUseCaseProvider);
  }

  public static AddExpenseViewModel newInstance(AddExpenseUseCase addExpenseUseCase,
      CheckBudgetAfterExpenseUseCase checkBudgetAfterExpenseUseCase) {
    return new AddExpenseViewModel(addExpenseUseCase, checkBudgetAfterExpenseUseCase);
  }
}
