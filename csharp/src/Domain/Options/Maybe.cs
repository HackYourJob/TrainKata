using System;

namespace TrainKata.Domain.Options
{
    public interface Maybe<T>
    {
        Maybe<TResult> Map<TResult>(Func<T, Maybe<TResult>> apply);

        T OrDefault(T defaultValue);
    }

    public static class Maybe
    {
        public static Maybe<T> Some<T>(T value)
        {
            return new Some<T>(value);
        }

        public static Maybe<T> None<T>()
        {
            return new None<T>();
        }
    }
}