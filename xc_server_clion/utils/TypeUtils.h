//
// Created by pixboly on 2019/1/9.
//

#ifndef XC_SERVER_CLION_TYPEUTILS_H
#define XC_SERVER_CLION_TYPEUTILS_H

#include <string>
#include <sstream>

using namespace std;
class TypeUtils {
public:
    static string ltos(long l)
    {
        ostringstream os;
        os<<l;
        string result;
        istringstream is(os.str());
        is>>result;
        return result;

    }
};

#endif //XC_SERVER_CLION_TYPEUTILS_H
