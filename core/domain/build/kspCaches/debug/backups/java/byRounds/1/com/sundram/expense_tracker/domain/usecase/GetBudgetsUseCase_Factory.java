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
public final class GetBudgetsUseCase_Factory implements Factory<GetBudgetsUseCase> {
  private final Provider<BudgetRepository> repositoryProvider;

  public GetBudgetsUseCase_Factory(Provider<BudgetRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetBudgetsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetBudgetsUseCase_Factory create(Provider<BudgetRepository> repositoryProvider) {
    return new GetBudgetsUseCase_Factory(repositoryProvider);
  }

  public static GetBudgetsUseCase newInstance(BudgetRepository repository) {
    return new GetBudgetsUseCase(repository);
  }
}
