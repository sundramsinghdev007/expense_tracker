package com.sundram.expense_tracker.data.di;

import com.sundram.expense_tracker.data.local.ExpenseTrackerDatabase;
import com.sundram.expense_tracker.data.local.dao.ExpenseDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideExpenseDaoFactory implements Factory<ExpenseDao> {
  private final Provider<ExpenseTrackerDatabase> dbProvider;

  public DatabaseModule_ProvideExpenseDaoFactory(Provider<ExpenseTrackerDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ExpenseDao get() {
    return provideExpenseDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideExpenseDaoFactory create(
      Provider<ExpenseTrackerDatabase> dbProvider) {
    return new DatabaseModule_ProvideExpenseDaoFactory(dbProvider);
  }

  public static ExpenseDao provideExpenseDao(ExpenseTrackerDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExpenseDao(db));
  }
}
