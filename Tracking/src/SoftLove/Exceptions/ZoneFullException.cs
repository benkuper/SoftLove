using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SoftLove.Exceptions
{
    [Serializable]
    class ZoneFullException : Exception
    {
        public ZoneFullException(string message) : base(message)
        {
        }
    }
}
