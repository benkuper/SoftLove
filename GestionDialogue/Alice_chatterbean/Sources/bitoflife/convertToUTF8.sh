# encodage de depart
encodeFrom='ISO-8859-1'
# encodage voulu
encodeTo='UTF-8'
# application du script sur les fichiers *.java
for filename in ` find . -type f -name *.java`
do    
    # sauvegarde du fichier source
    mv $filename $filename.save
    # ecriture du fichier encode
    iconv -f $encodeFrom -t $encodeTo $filename.save -o $filename
done