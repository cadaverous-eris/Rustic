
// create vase blockstate variants
if (false) console.log([...Array(13).keys()].map((variant) => {
return `${variant}: {
	"model": "rustic:vase_${(variant < 8) ? 0 : 1}",
	"textures": {
		"side": "rustic:blocks/vases/vase_side_${variant}",
		"top": "rustic:blocks/vases/vase_top_${variant}",
		"bottom": "rustic:blocks/vases/vase_bottom_${variant}"
	}
}`;
}).join(',\n'));


// create vase item models
const fs = require('fs');
[...Array(13).keys()].forEach(variant => {
	const model =
`{
	"parent": "rustic:block/vase_${(variant < 8) ? 0 : 1}",
	"textures": {
		"side": "rustic:blocks/vases/vase_side_${variant}",
		"top": "rustic:blocks/vases/vase_top_${variant}",
		"bottom": "rustic:blocks/vases/vase_bottom_${variant}"
	}
}`;

	fs.writeFileSync(`./models/item/vase_${variant}.json`, model);
});
