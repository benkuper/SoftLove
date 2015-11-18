using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SoftLove.Exceptions
{
    [Serializable]
    class NumberOfPointsException : Exception
    {
        public NumberOfPointsException(string message) : base(message)
        {
        }
    }
}
