export ISO_FILE=$1
export INPUT_FOLDER=$2
export VOLUME_NAME=$3
mkisofs -iso-level 3 -J -joliet-long -rock -input-charset utf-8 -V $VOLUME_NAME -r -o $ISO_FILE $INPUT_FOLDER/
