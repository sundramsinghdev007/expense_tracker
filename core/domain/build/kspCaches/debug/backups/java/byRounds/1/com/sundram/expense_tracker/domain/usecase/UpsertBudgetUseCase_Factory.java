package com.sundram.expense_tracker.domain.usecase;

import com.sundram.expense_tracker.domain.repository.BudgetRepository;
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
public final class UpsertBudgetUseCase_Factory implements Factory<UpsertBudgetUseCase> {
  private final Provider<BudgetRepository> repositoryProvider;

  public UpsertBudgetUseCase_Factory(Provider<BudgetRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpsertBudgetUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpsertBudgetUseCase_Factory create(Provider<BudgetRepository> repositoryProvider) {
    return new UpsertBudgetUseCase_Factory(repositoryProvider);
  }

  public static UpsertBudgetUseCase newInstance(BudgetRepository repository) {
    return new UpsertBudgetUseCase(repository);
  }
}
