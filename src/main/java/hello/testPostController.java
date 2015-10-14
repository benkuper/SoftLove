package hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class testPostController {

    @RequestMapping(value="/testPost", method = RequestMethod.POST)
    public void testPost(@RequestParam(value="param", defaultValue="") String param) {
	System.out.println(" : "+param);
    }
}
