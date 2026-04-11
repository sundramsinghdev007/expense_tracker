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
public final class GetExpensesUseCase_Factory implements Factory<GetExpensesUseCase> {
  private final Provider<ExpenseRepository> repositoryProvider;

  public GetExpensesUseCase_Factory(Provider<ExpenseRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetExpensesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetExpensesUseCase_Factory create(Provider<ExpenseRepository> repositoryProvider) {
    return new GetExpensesUseCase_Factory(repositoryProvider);
  }

  public static GetExpensesUseCase newInstance(ExpenseRepository repository) {
    return new GetExpensesUseCase(repository);
  }
}
