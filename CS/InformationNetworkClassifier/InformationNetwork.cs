using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace InformationNetworkClassifier {
    class InformationNetwork {
        private List<Attribute> attributes;
        private List<DataRecord> records;
        /**
            Ham nay doc du lieu hoc tu file .txt
            File du lieu co cau truc duoc mo ta theo muc 3.2 tai lieu ITCM.docx
        */
        public void readDataFromFile(string filename)  {
            string line;
            using (var fileStream = new StreamReader(filename)) {
                while( (line = fileStream.ReadLine()) != null ) {

                }
            }
        }
    }
}
