package com.sundram.expense_tracker.domain.usecase;

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
public final class UpdateExpenseUseCase_Factory implements Factory<UpdateExpenseUseCase> {
  private final Provider<ExpenseRepository> repositoryProvider;

  public UpdateExpenseUseCase_Factory(Provider<ExpenseRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateExpenseUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateExpenseUseCase_Factory create(
      Provider<ExpenseRepository> repositoryProvider) {
    return new UpdateExpenseUseCase_Factory(repositoryProvider);
  }

  public static UpdateExpenseUseCase newInstance(ExpenseRepository repository) {
    return new UpdateExpenseUseCase(repository);
  }
}
