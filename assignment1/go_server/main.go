package main

import (
	"strconv"
	"regexp"
	"image"
	_ "image/jpeg"
	_ "image/png"
	"net/http"
	"github.com/gin-gonic/gin"
	"example/go_server/mystruct"
)

func main() {
	router := gin.Default()
	router.POST("/albums", newAlbum)
	router.GET("/albums/:albumID", getAlbumByKey)
	//need to change to enable the server to run on EC2
	router.Run(":8080")
}

// Returns the new key and size of an image in bytes.
func newAlbum(c *gin.Context) {
		c.Header("Content-Type", "application/json; charset=UTF-8")

		errorMsg := mystruct.ErrorMsg{
			Msg: "invalid request",
		}

		form, err := c.MultipartForm()
		if err != nil {
			c.IndentedJSON(http.StatusBadRequest, errorMsg)
			return
		}

		imageFiles := form.File["image"]
		if len(imageFiles) == 0 {
			c.IndentedJSON(http.StatusBadRequest, errorMsg)
			return
		}

		imageFile, err := imageFiles[0].Open()
    if err != nil {
        c.IndentedJSON(http.StatusBadRequest, errorMsg)
        return
    }
    defer imageFile.Close()

    imageConfig, _, _ := image.DecodeConfig(imageFile)
		
		imageMetaData := mystruct.ImageMetaData{
			AlbumID: "4",
			ImageSize: strconv.Itoa(imageConfig.Height * imageConfig.Width),
		}
		c.IndentedJSON(http.StatusCreated, imageMetaData)
}

// get album by key
func getAlbumByKey(c *gin.Context) {
	c.Header("Content-Type", "application/json; charset=UTF-8")

	id := c.Param("albumID")
	digitPattern := "^[0-9]+$"

	isDigits, err := regexp.MatchString(digitPattern, id)
	if !isDigits || err != nil {
		invalidError := mystruct.ErrorMsg{
			Msg: "invalid request",
		}
			c.IndentedJSON(http.StatusBadRequest, invalidError)
      return
	}

	album := mystruct.AlbumInfo{
		Artist: "Sex Pistols",
		Title: "Never Mind The Bollocks!",
		Year: "1977",
	}
	c.IndentedJSON(http.StatusOK, album)
}
