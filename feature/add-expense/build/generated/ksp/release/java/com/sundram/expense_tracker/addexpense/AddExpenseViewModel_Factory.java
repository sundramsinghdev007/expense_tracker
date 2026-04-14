package com.sundram.expense_tracker.addexpense;

import com.sundram.expense_tracker.domain.usecase.AddExpenseUseCase;
import com.sundram.expense_tracker.domain.usecase.CheckBudgetAfterExpenseUseCase;
import com.sundram.expense_tracker.domain.usecase.DeleteExpenseUseCase;
import com.sundram.expense_tracker.domain.usecase.GetExpenseByIdUseCase;
import com.sundram.expense_tracker.domain.usecase.UpdateExpenseUseCase;
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

  private final Provider<GetExpenseByIdUseCase> getExpenseByIdUseCaseProvider;

  private final Provider<UpdateExpenseUseCase> updateExpenseUseCaseProvider;

  private final Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider;

  public AddExpenseViewModel_Factory(Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<CheckBudgetAfterExpenseUseCase> checkBudgetAfterExpenseUseCaseProvider,
      Provider<GetExpenseByIdUseCase> getExpenseByIdUseCaseProvider,
      Provider<UpdateExpenseUseCase> updateExpenseUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    this.addExpenseUseCaseProvider = addExpenseUseCaseProvider;
    this.checkBudgetAfterExpenseUseCaseProvider = checkBudgetAfterExpenseUseCaseProvider;
    this.getExpenseByIdUseCaseProvider = getExpenseByIdUseCaseProvider;
    this.updateExpenseUseCaseProvider = updateExpenseUseCaseProvider;
    this.deleteExpenseUseCaseProvider = deleteExpenseUseCaseProvider;
  }

  @Override
  public AddExpenseViewModel get() {
    return newInstance(addExpenseUseCaseProvider.get(), checkBudgetAfterExpenseUseCaseProvider.get(), getExpenseByIdUseCaseProvider.get(), updateExpenseUseCaseProvider.get(), deleteExpenseUseCaseProvider.get());
  }

  public static AddExpenseViewModel_Factory create(
      Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<CheckBudgetAfterExpenseUseCase> checkBudgetAfterExpenseUseCaseProvider,
      Provider<GetExpenseByIdUseCase> getExpenseByIdUseCaseProvider,
      Provider<UpdateExpenseUseCase> updateExpenseUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider) {
    return new AddExpenseViewModel_Factory(addExpenseUseCaseProvider, checkBudgetAfterExpenseUseCaseProvider, getExpenseByIdUseCaseProvider, updateExpenseUseCaseProvider, deleteExpenseUseCaseProvider);
  }

  public static AddExpenseViewModel newInstance(AddExpenseUseCase addExpenseUseCase,
      CheckBudgetAfterExpenseUseCase checkBudgetAfterExpenseUseCase,
      GetExpenseByIdUseCase getExpenseByIdUseCase, UpdateExpenseUseCase updateExpenseUseCase,
      DeleteExpenseUseCase deleteExpenseUseCase) {
    return new AddExpenseViewModel(addExpenseUseCase, checkBudgetAfterExpenseUseCase, getExpenseByIdUseCase, updateExpenseUseCase, deleteExpenseUseCase);
  }
}
