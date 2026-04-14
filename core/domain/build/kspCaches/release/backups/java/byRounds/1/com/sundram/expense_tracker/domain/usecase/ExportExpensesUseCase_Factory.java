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
public final class ExportExpensesUseCase_Factory implements Factory<ExportExpensesUseCase> {
  private final Provider<ExpenseRepository> repositoryProvider;

  public ExportExpensesUseCase_Factory(Provider<ExpenseRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ExportExpensesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ExportExpensesUseCase_Factory create(
      Provider<ExpenseRepository> repositoryProvider) {
    return new ExportExpensesUseCase_Factory(repositoryProvider);
  }

  public static ExportExpensesUseCase newInstance(ExpenseRepository repository) {
    return new ExportExpensesUseCase(repository);
  }
}
