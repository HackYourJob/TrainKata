using System;

namespace TrainKata.Domain.Options
{
    public class None<T> : Maybe<T>
    {
        public Maybe<TResult> Map<TResult>(Func<T, Maybe<TResult>> apply)
        {
            return new None<TResult>();
        }

        public T OrDefault(T defaultValue)
        {
            return defaultValue;
        }

        public bool HasValue()
        {
            return false;
        }
    }
}